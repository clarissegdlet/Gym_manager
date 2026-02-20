import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IMember } from 'app/entities/member/member.model';
import { MemberService } from 'app/entities/member/service/member.service';
import { MembershipStatus } from 'app/entities/enumerations/membership-status.model';
import { MembershipService } from '../service/membership.service';
import { IMembership } from '../membership.model';
import { MembershipFormGroup, MembershipFormService } from './membership-form.service';

@Component({
  selector: 'jhi-membership-update',
  templateUrl: './membership-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class MembershipUpdateComponent implements OnInit {
  isSaving = false;
  membership: IMembership | null = null;
  membershipStatusValues = Object.keys(MembershipStatus);

  membersSharedCollection: IMember[] = [];

  protected membershipService = inject(MembershipService);
  protected membershipFormService = inject(MembershipFormService);
  protected memberService = inject(MemberService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: MembershipFormGroup = this.membershipFormService.createMembershipFormGroup();

  compareMember = (o1: IMember | null, o2: IMember | null): boolean => this.memberService.compareMember(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ membership }) => {
      this.membership = membership;
      if (membership) {
        this.updateForm(membership);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const membership = this.membershipFormService.getMembership(this.editForm);
    if (membership.id !== null) {
      this.subscribeToSaveResponse(this.membershipService.update(membership));
    } else {
      this.subscribeToSaveResponse(this.membershipService.create(membership));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMembership>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(membership: IMembership): void {
    this.membership = membership;
    this.membershipFormService.resetForm(this.editForm, membership);

    this.membersSharedCollection = this.memberService.addMemberToCollectionIfMissing<IMember>(
      this.membersSharedCollection,
      membership.member,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.memberService
      .query()
      .pipe(map((res: HttpResponse<IMember[]>) => res.body ?? []))
      .pipe(map((members: IMember[]) => this.memberService.addMemberToCollectionIfMissing<IMember>(members, this.membership?.member)))
      .subscribe((members: IMember[]) => (this.membersSharedCollection = members));
  }
}
