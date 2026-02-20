import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ICoach } from '../coach.model';
import { CoachService } from '../service/coach.service';
import { CoachFormGroup, CoachFormService } from './coach-form.service';

@Component({
  selector: 'jhi-coach-update',
  templateUrl: './coach-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class CoachUpdateComponent implements OnInit {
  isSaving = false;
  coach: ICoach | null = null;

  protected coachService = inject(CoachService);
  protected coachFormService = inject(CoachFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CoachFormGroup = this.coachFormService.createCoachFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ coach }) => {
      this.coach = coach;
      if (coach) {
        this.updateForm(coach);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const coach = this.coachFormService.getCoach(this.editForm);
    if (coach.id !== null) {
      this.subscribeToSaveResponse(this.coachService.update(coach));
    } else {
      this.subscribeToSaveResponse(this.coachService.create(coach));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICoach>>): void {
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

  protected updateForm(coach: ICoach): void {
    this.coach = coach;
    this.coachFormService.resetForm(this.editForm, coach);
  }
}
