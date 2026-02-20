import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ICoach } from 'app/entities/coach/coach.model';
import { CoachService } from 'app/entities/coach/service/coach.service';
import { IRoom } from 'app/entities/room/room.model';
import { RoomService } from 'app/entities/room/service/room.service';
import { ClassStatus } from 'app/entities/enumerations/class-status.model';
import { ClassSessionService } from '../service/class-session.service';
import { IClassSession } from '../class-session.model';
import { ClassSessionFormGroup, ClassSessionFormService } from './class-session-form.service';

@Component({
  selector: 'jhi-class-session-update',
  templateUrl: './class-session-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ClassSessionUpdateComponent implements OnInit {
  isSaving = false;
  classSession: IClassSession | null = null;
  classStatusValues = Object.keys(ClassStatus);

  coachesSharedCollection: ICoach[] = [];
  roomsSharedCollection: IRoom[] = [];

  protected classSessionService = inject(ClassSessionService);
  protected classSessionFormService = inject(ClassSessionFormService);
  protected coachService = inject(CoachService);
  protected roomService = inject(RoomService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ClassSessionFormGroup = this.classSessionFormService.createClassSessionFormGroup();

  compareCoach = (o1: ICoach | null, o2: ICoach | null): boolean => this.coachService.compareCoach(o1, o2);

  compareRoom = (o1: IRoom | null, o2: IRoom | null): boolean => this.roomService.compareRoom(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ classSession }) => {
      this.classSession = classSession;
      if (classSession) {
        this.updateForm(classSession);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const classSession = this.classSessionFormService.getClassSession(this.editForm);
    if (classSession.id !== null) {
      this.subscribeToSaveResponse(this.classSessionService.update(classSession));
    } else {
      this.subscribeToSaveResponse(this.classSessionService.create(classSession));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IClassSession>>): void {
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

  protected updateForm(classSession: IClassSession): void {
    this.classSession = classSession;
    this.classSessionFormService.resetForm(this.editForm, classSession);

    this.coachesSharedCollection = this.coachService.addCoachToCollectionIfMissing<ICoach>(
      this.coachesSharedCollection,
      classSession.coach,
    );
    this.roomsSharedCollection = this.roomService.addRoomToCollectionIfMissing<IRoom>(this.roomsSharedCollection, classSession.room);
  }

  protected loadRelationshipsOptions(): void {
    this.coachService
      .query()
      .pipe(map((res: HttpResponse<ICoach[]>) => res.body ?? []))
      .pipe(map((coaches: ICoach[]) => this.coachService.addCoachToCollectionIfMissing<ICoach>(coaches, this.classSession?.coach)))
      .subscribe((coaches: ICoach[]) => (this.coachesSharedCollection = coaches));

    this.roomService
      .query()
      .pipe(map((res: HttpResponse<IRoom[]>) => res.body ?? []))
      .pipe(map((rooms: IRoom[]) => this.roomService.addRoomToCollectionIfMissing<IRoom>(rooms, this.classSession?.room)))
      .subscribe((rooms: IRoom[]) => (this.roomsSharedCollection = rooms));
  }
}
