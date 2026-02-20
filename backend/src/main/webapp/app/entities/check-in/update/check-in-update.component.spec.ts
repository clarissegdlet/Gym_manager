import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IMember } from 'app/entities/member/member.model';
import { MemberService } from 'app/entities/member/service/member.service';
import { CheckInService } from '../service/check-in.service';
import { ICheckIn } from '../check-in.model';
import { CheckInFormService } from './check-in-form.service';

import { CheckInUpdateComponent } from './check-in-update.component';

describe('CheckIn Management Update Component', () => {
  let comp: CheckInUpdateComponent;
  let fixture: ComponentFixture<CheckInUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let checkInFormService: CheckInFormService;
  let checkInService: CheckInService;
  let memberService: MemberService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CheckInUpdateComponent],
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
      .overrideTemplate(CheckInUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CheckInUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    checkInFormService = TestBed.inject(CheckInFormService);
    checkInService = TestBed.inject(CheckInService);
    memberService = TestBed.inject(MemberService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Member query and add missing value', () => {
      const checkIn: ICheckIn = { id: 20452 };
      const member: IMember = { id: 17514 };
      checkIn.member = member;

      const memberCollection: IMember[] = [{ id: 17514 }];
      jest.spyOn(memberService, 'query').mockReturnValue(of(new HttpResponse({ body: memberCollection })));
      const additionalMembers = [member];
      const expectedCollection: IMember[] = [...additionalMembers, ...memberCollection];
      jest.spyOn(memberService, 'addMemberToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ checkIn });
      comp.ngOnInit();

      expect(memberService.query).toHaveBeenCalled();
      expect(memberService.addMemberToCollectionIfMissing).toHaveBeenCalledWith(
        memberCollection,
        ...additionalMembers.map(expect.objectContaining),
      );
      expect(comp.membersSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const checkIn: ICheckIn = { id: 20452 };
      const member: IMember = { id: 17514 };
      checkIn.member = member;

      activatedRoute.data = of({ checkIn });
      comp.ngOnInit();

      expect(comp.membersSharedCollection).toContainEqual(member);
      expect(comp.checkIn).toEqual(checkIn);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICheckIn>>();
      const checkIn = { id: 14585 };
      jest.spyOn(checkInFormService, 'getCheckIn').mockReturnValue(checkIn);
      jest.spyOn(checkInService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ checkIn });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: checkIn }));
      saveSubject.complete();

      // THEN
      expect(checkInFormService.getCheckIn).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(checkInService.update).toHaveBeenCalledWith(expect.objectContaining(checkIn));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICheckIn>>();
      const checkIn = { id: 14585 };
      jest.spyOn(checkInFormService, 'getCheckIn').mockReturnValue({ id: null });
      jest.spyOn(checkInService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ checkIn: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: checkIn }));
      saveSubject.complete();

      // THEN
      expect(checkInFormService.getCheckIn).toHaveBeenCalled();
      expect(checkInService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICheckIn>>();
      const checkIn = { id: 14585 };
      jest.spyOn(checkInService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ checkIn });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(checkInService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareMember', () => {
      it('should forward to memberService', () => {
        const entity = { id: 17514 };
        const entity2 = { id: 30790 };
        jest.spyOn(memberService, 'compareMember');
        comp.compareMember(entity, entity2);
        expect(memberService.compareMember).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
