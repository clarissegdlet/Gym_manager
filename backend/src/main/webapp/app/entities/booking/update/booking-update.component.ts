import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IMember } from 'app/entities/member/member.model';
import { MemberService } from 'app/entities/member/service/member.service';
import { IClassSession } from 'app/entities/class-session/class-session.model';
import { ClassSessionService } from 'app/entities/class-session/service/class-session.service';
import { BookingStatus } from 'app/entities/enumerations/booking-status.model';
import { BookingService } from '../service/booking.service';
import { IBooking } from '../booking.model';
import { BookingFormGroup, BookingFormService } from './booking-form.service';

@Component({
  selector: 'jhi-booking-update',
  templateUrl: './booking-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class BookingUpdateComponent implements OnInit {
  isSaving = false;
  booking: IBooking | null = null;
  bookingStatusValues = Object.keys(BookingStatus);

  membersSharedCollection: IMember[] = [];
  classSessionsSharedCollection: IClassSession[] = [];

  protected bookingService = inject(BookingService);
  protected bookingFormService = inject(BookingFormService);
  protected memberService = inject(MemberService);
  protected classSessionService = inject(ClassSessionService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: BookingFormGroup = this.bookingFormService.createBookingFormGroup();

  compareMember = (o1: IMember | null, o2: IMember | null): boolean => this.memberService.compareMember(o1, o2);

  compareClassSession = (o1: IClassSession | null, o2: IClassSession | null): boolean =>
    this.classSessionService.compareClassSession(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ booking }) => {
      this.booking = booking;
      if (booking) {
        this.updateForm(booking);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const booking = this.bookingFormService.getBooking(this.editForm);
    if (booking.id !== null) {
      this.subscribeToSaveResponse(this.bookingService.update(booking));
    } else {
      this.subscribeToSaveResponse(this.bookingService.create(booking));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBooking>>): void {
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

  protected updateForm(booking: IBooking): void {
    this.booking = booking;
    this.bookingFormService.resetForm(this.editForm, booking);

    this.membersSharedCollection = this.memberService.addMemberToCollectionIfMissing<IMember>(this.membersSharedCollection, booking.member);
    this.classSessionsSharedCollection = this.classSessionService.addClassSessionToCollectionIfMissing<IClassSession>(
      this.classSessionsSharedCollection,
      booking.classSession,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.memberService
      .query()
      .pipe(map((res: HttpResponse<IMember[]>) => res.body ?? []))
      .pipe(map((members: IMember[]) => this.memberService.addMemberToCollectionIfMissing<IMember>(members, this.booking?.member)))
      .subscribe((members: IMember[]) => (this.membersSharedCollection = members));

    this.classSessionService
      .query()
      .pipe(map((res: HttpResponse<IClassSession[]>) => res.body ?? []))
      .pipe(
        map((classSessions: IClassSession[]) =>
          this.classSessionService.addClassSessionToCollectionIfMissing<IClassSession>(classSessions, this.booking?.classSession),
        ),
      )
      .subscribe((classSessions: IClassSession[]) => (this.classSessionsSharedCollection = classSessions));
  }
}
