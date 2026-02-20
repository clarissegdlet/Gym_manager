import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IMember } from 'app/entities/member/member.model';
import { MemberService } from 'app/entities/member/service/member.service';
import { MembershipService } from '../service/membership.service';
import { IMembership } from '../membership.model';
import { MembershipFormService } from './membership-form.service';

import { MembershipUpdateComponent } from './membership-update.component';

describe('Membership Management Update Component', () => {
  let comp: MembershipUpdateComponent;
  let fixture: ComponentFixture<MembershipUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let membershipFormService: MembershipFormService;
  let membershipService: MembershipService;
  let memberService: MemberService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [MembershipUpdateComponent],
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
      .overrideTemplate(MembershipUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MembershipUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    membershipFormService = TestBed.inject(MembershipFormService);
    membershipService = TestBed.inject(MembershipService);
    memberService = TestBed.inject(MemberService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Member query and add missing value', () => {
      const membership: IMembership = { id: 26062 };
      const member: IMember = { id: 17514 };
      membership.member = member;

      const memberCollection: IMember[] = [{ id: 17514 }];
      jest.spyOn(memberService, 'query').mockReturnValue(of(new HttpResponse({ body: memberCollection })));
      const additionalMembers = [member];
      const expectedCollection: IMember[] = [...additionalMembers, ...memberCollection];
      jest.spyOn(memberService, 'addMemberToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ membership });
      comp.ngOnInit();

      expect(memberService.query).toHaveBeenCalled();
      expect(memberService.addMemberToCollectionIfMissing).toHaveBeenCalledWith(
        memberCollection,
        ...additionalMembers.map(expect.objectContaining),
      );
      expect(comp.membersSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const membership: IMembership = { id: 26062 };
      const member: IMember = { id: 17514 };
      membership.member = member;

      activatedRoute.data = of({ membership });
      comp.ngOnInit();

      expect(comp.membersSharedCollection).toContainEqual(member);
      expect(comp.membership).toEqual(membership);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMembership>>();
      const membership = { id: 10393 };
      jest.spyOn(membershipFormService, 'getMembership').mockReturnValue(membership);
      jest.spyOn(membershipService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ membership });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: membership }));
      saveSubject.complete();

      // THEN
      expect(membershipFormService.getMembership).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(membershipService.update).toHaveBeenCalledWith(expect.objectContaining(membership));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMembership>>();
      const membership = { id: 10393 };
      jest.spyOn(membershipFormService, 'getMembership').mockReturnValue({ id: null });
      jest.spyOn(membershipService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ membership: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: membership }));
      saveSubject.complete();

      // THEN
      expect(membershipFormService.getMembership).toHaveBeenCalled();
      expect(membershipService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMembership>>();
      const membership = { id: 10393 };
      jest.spyOn(membershipService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ membership });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(membershipService.update).toHaveBeenCalled();
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
