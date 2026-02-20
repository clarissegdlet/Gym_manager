import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ClassStatus } from 'app/entities/enumerations/class-status.model';
import { IClassSession } from '../class-session.model';
import { ClassSessionService } from '../service/class-session.service';
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

  protected classSessionService = inject(ClassSessionService);
  protected classSessionFormService = inject(ClassSessionFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ClassSessionFormGroup = this.classSessionFormService.createClassSessionFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ classSession }) => {
      this.classSession = classSession;
      if (classSession) {
        this.updateForm(classSession);
      }
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
  }
}
