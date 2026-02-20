import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { CoachService } from '../service/coach.service';
import { ICoach } from '../coach.model';
import { CoachFormService } from './coach-form.service';

import { CoachUpdateComponent } from './coach-update.component';

describe('Coach Management Update Component', () => {
  let comp: CoachUpdateComponent;
  let fixture: ComponentFixture<CoachUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let coachFormService: CoachFormService;
  let coachService: CoachService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CoachUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(CoachUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CoachUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    coachFormService = TestBed.inject(CoachFormService);
    coachService = TestBed.inject(CoachService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const coach: ICoach = { id: 31551 };

      activatedRoute.data = of({ coach });
      comp.ngOnInit();

      expect(comp.coach).toEqual(coach);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICoach>>();
      const coach = { id: 24583 };
      jest.spyOn(coachFormService, 'getCoach').mockReturnValue(coach);
      jest.spyOn(coachService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ coach });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: coach }));
      saveSubject.complete();

      // THEN
      expect(coachFormService.getCoach).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(coachService.update).toHaveBeenCalledWith(expect.objectContaining(coach));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICoach>>();
      const coach = { id: 24583 };
      jest.spyOn(coachFormService, 'getCoach').mockReturnValue({ id: null });
      jest.spyOn(coachService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ coach: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: coach }));
      saveSubject.complete();

      // THEN
      expect(coachFormService.getCoach).toHaveBeenCalled();
      expect(coachService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICoach>>();
      const coach = { id: 24583 };
      jest.spyOn(coachService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ coach });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(coachService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
