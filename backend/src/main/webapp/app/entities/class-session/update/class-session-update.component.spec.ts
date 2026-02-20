import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ClassSessionService } from '../service/class-session.service';
import { IClassSession } from '../class-session.model';
import { ClassSessionFormService } from './class-session-form.service';

import { ClassSessionUpdateComponent } from './class-session-update.component';

describe('ClassSession Management Update Component', () => {
  let comp: ClassSessionUpdateComponent;
  let fixture: ComponentFixture<ClassSessionUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let classSessionFormService: ClassSessionFormService;
  let classSessionService: ClassSessionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ClassSessionUpdateComponent],
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
      .overrideTemplate(ClassSessionUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ClassSessionUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    classSessionFormService = TestBed.inject(ClassSessionFormService);
    classSessionService = TestBed.inject(ClassSessionService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const classSession: IClassSession = { id: 8832 };

      activatedRoute.data = of({ classSession });
      comp.ngOnInit();

      expect(comp.classSession).toEqual(classSession);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IClassSession>>();
      const classSession = { id: 18095 };
      jest.spyOn(classSessionFormService, 'getClassSession').mockReturnValue(classSession);
      jest.spyOn(classSessionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ classSession });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: classSession }));
      saveSubject.complete();

      // THEN
      expect(classSessionFormService.getClassSession).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(classSessionService.update).toHaveBeenCalledWith(expect.objectContaining(classSession));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IClassSession>>();
      const classSession = { id: 18095 };
      jest.spyOn(classSessionFormService, 'getClassSession').mockReturnValue({ id: null });
      jest.spyOn(classSessionService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ classSession: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: classSession }));
      saveSubject.complete();

      // THEN
      expect(classSessionFormService.getClassSession).toHaveBeenCalled();
      expect(classSessionService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IClassSession>>();
      const classSession = { id: 18095 };
      jest.spyOn(classSessionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ classSession });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(classSessionService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
