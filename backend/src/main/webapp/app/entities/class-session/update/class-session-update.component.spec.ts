import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ICoach } from 'app/entities/coach/coach.model';
import { CoachService } from 'app/entities/coach/service/coach.service';
import { IRoom } from 'app/entities/room/room.model';
import { RoomService } from 'app/entities/room/service/room.service';
import { IClassSession } from '../class-session.model';
import { ClassSessionService } from '../service/class-session.service';
import { ClassSessionFormService } from './class-session-form.service';

import { ClassSessionUpdateComponent } from './class-session-update.component';

describe('ClassSession Management Update Component', () => {
  let comp: ClassSessionUpdateComponent;
  let fixture: ComponentFixture<ClassSessionUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let classSessionFormService: ClassSessionFormService;
  let classSessionService: ClassSessionService;
  let coachService: CoachService;
  let roomService: RoomService;

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
    coachService = TestBed.inject(CoachService);
    roomService = TestBed.inject(RoomService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Coach query and add missing value', () => {
      const classSession: IClassSession = { id: 8832 };
      const coach: ICoach = { id: 24583 };
      classSession.coach = coach;

      const coachCollection: ICoach[] = [{ id: 24583 }];
      jest.spyOn(coachService, 'query').mockReturnValue(of(new HttpResponse({ body: coachCollection })));
      const additionalCoaches = [coach];
      const expectedCollection: ICoach[] = [...additionalCoaches, ...coachCollection];
      jest.spyOn(coachService, 'addCoachToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ classSession });
      comp.ngOnInit();

      expect(coachService.query).toHaveBeenCalled();
      expect(coachService.addCoachToCollectionIfMissing).toHaveBeenCalledWith(
        coachCollection,
        ...additionalCoaches.map(expect.objectContaining),
      );
      expect(comp.coachesSharedCollection).toEqual(expectedCollection);
    });

    it('should call Room query and add missing value', () => {
      const classSession: IClassSession = { id: 8832 };
      const room: IRoom = { id: 31469 };
      classSession.room = room;

      const roomCollection: IRoom[] = [{ id: 31469 }];
      jest.spyOn(roomService, 'query').mockReturnValue(of(new HttpResponse({ body: roomCollection })));
      const additionalRooms = [room];
      const expectedCollection: IRoom[] = [...additionalRooms, ...roomCollection];
      jest.spyOn(roomService, 'addRoomToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ classSession });
      comp.ngOnInit();

      expect(roomService.query).toHaveBeenCalled();
      expect(roomService.addRoomToCollectionIfMissing).toHaveBeenCalledWith(
        roomCollection,
        ...additionalRooms.map(expect.objectContaining),
      );
      expect(comp.roomsSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const classSession: IClassSession = { id: 8832 };
      const coach: ICoach = { id: 24583 };
      classSession.coach = coach;
      const room: IRoom = { id: 31469 };
      classSession.room = room;

      activatedRoute.data = of({ classSession });
      comp.ngOnInit();

      expect(comp.coachesSharedCollection).toContainEqual(coach);
      expect(comp.roomsSharedCollection).toContainEqual(room);
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

  describe('Compare relationships', () => {
    describe('compareCoach', () => {
      it('should forward to coachService', () => {
        const entity = { id: 24583 };
        const entity2 = { id: 31551 };
        jest.spyOn(coachService, 'compareCoach');
        comp.compareCoach(entity, entity2);
        expect(coachService.compareCoach).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareRoom', () => {
      it('should forward to roomService', () => {
        const entity = { id: 31469 };
        const entity2 = { id: 22394 };
        jest.spyOn(roomService, 'compareRoom');
        comp.compareRoom(entity, entity2);
        expect(roomService.compareRoom).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
