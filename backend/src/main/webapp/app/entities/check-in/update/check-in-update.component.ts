import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IMember } from 'app/entities/member/member.model';
import { MemberService } from 'app/entities/member/service/member.service';
import { ICheckIn } from '../check-in.model';
import { CheckInService } from '../service/check-in.service';
import { CheckInFormGroup, CheckInFormService } from './check-in-form.service';

@Component({
  selector: 'jhi-check-in-update',
  templateUrl: './check-in-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class CheckInUpdateComponent implements OnInit {
  isSaving = false;
  checkIn: ICheckIn | null = null;

  membersSharedCollection: IMember[] = [];

  protected checkInService = inject(CheckInService);
  protected checkInFormService = inject(CheckInFormService);
  protected memberService = inject(MemberService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CheckInFormGroup = this.checkInFormService.createCheckInFormGroup();

  compareMember = (o1: IMember | null, o2: IMember | null): boolean => this.memberService.compareMember(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ checkIn }) => {
      this.checkIn = checkIn;
      if (checkIn) {
        this.updateForm(checkIn);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const checkIn = this.checkInFormService.getCheckIn(this.editForm);
    if (checkIn.id !== null) {
      this.subscribeToSaveResponse(this.checkInService.update(checkIn));
    } else {
      this.subscribeToSaveResponse(this.checkInService.create(checkIn));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICheckIn>>): void {
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

  protected updateForm(checkIn: ICheckIn): void {
    this.checkIn = checkIn;
    this.checkInFormService.resetForm(this.editForm, checkIn);

    this.membersSharedCollection = this.memberService.addMemberToCollectionIfMissing<IMember>(this.membersSharedCollection, checkIn.member);
  }

  protected loadRelationshipsOptions(): void {
    this.memberService
      .query()
      .pipe(map((res: HttpResponse<IMember[]>) => res.body ?? []))
      .pipe(map((members: IMember[]) => this.memberService.addMemberToCollectionIfMissing<IMember>(members, this.checkIn?.member)))
      .subscribe((members: IMember[]) => (this.membersSharedCollection = members));
  }
}
