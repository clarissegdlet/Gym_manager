import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IClassSession, NewClassSession } from '../class-session.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IClassSession for edit and NewClassSessionFormGroupInput for create.
 */
type ClassSessionFormGroupInput = IClassSession | PartialWithRequiredKeyOf<NewClassSession>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IClassSession | NewClassSession> = Omit<T, 'dateTime'> & {
  dateTime?: string | null;
};

type ClassSessionFormRawValue = FormValueOf<IClassSession>;

type NewClassSessionFormRawValue = FormValueOf<NewClassSession>;

type ClassSessionFormDefaults = Pick<NewClassSession, 'id' | 'dateTime'>;

type ClassSessionFormGroupContent = {
  id: FormControl<ClassSessionFormRawValue['id'] | NewClassSession['id']>;
  title: FormControl<ClassSessionFormRawValue['title']>;
  dateTime: FormControl<ClassSessionFormRawValue['dateTime']>;
  capacity: FormControl<ClassSessionFormRawValue['capacity']>;
  status: FormControl<ClassSessionFormRawValue['status']>;
  coach: FormControl<ClassSessionFormRawValue['coach']>;
  room: FormControl<ClassSessionFormRawValue['room']>;
};

export type ClassSessionFormGroup = FormGroup<ClassSessionFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ClassSessionFormService {
  createClassSessionFormGroup(classSession: ClassSessionFormGroupInput = { id: null }): ClassSessionFormGroup {
    const classSessionRawValue = this.convertClassSessionToClassSessionRawValue({
      ...this.getFormDefaults(),
      ...classSession,
    });
    return new FormGroup<ClassSessionFormGroupContent>({
      id: new FormControl(
        { value: classSessionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      title: new FormControl(classSessionRawValue.title, {
        validators: [Validators.required],
      }),
      dateTime: new FormControl(classSessionRawValue.dateTime, {
        validators: [Validators.required],
      }),
      capacity: new FormControl(classSessionRawValue.capacity, {
        validators: [Validators.required],
      }),
      status: new FormControl(classSessionRawValue.status, {
        validators: [Validators.required],
      }),
      coach: new FormControl(classSessionRawValue.coach),
      room: new FormControl(classSessionRawValue.room),
    });
  }

  getClassSession(form: ClassSessionFormGroup): IClassSession | NewClassSession {
    return this.convertClassSessionRawValueToClassSession(form.getRawValue() as ClassSessionFormRawValue | NewClassSessionFormRawValue);
  }

  resetForm(form: ClassSessionFormGroup, classSession: ClassSessionFormGroupInput): void {
    const classSessionRawValue = this.convertClassSessionToClassSessionRawValue({ ...this.getFormDefaults(), ...classSession });
    form.reset(
      {
        ...classSessionRawValue,
        id: { value: classSessionRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ClassSessionFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      dateTime: currentTime,
    };
  }

  private convertClassSessionRawValueToClassSession(
    rawClassSession: ClassSessionFormRawValue | NewClassSessionFormRawValue,
  ): IClassSession | NewClassSession {
    return {
      ...rawClassSession,
      dateTime: dayjs(rawClassSession.dateTime, DATE_TIME_FORMAT),
    };
  }

  private convertClassSessionToClassSessionRawValue(
    classSession: IClassSession | (Partial<NewClassSession> & ClassSessionFormDefaults),
  ): ClassSessionFormRawValue | PartialWithRequiredKeyOf<NewClassSessionFormRawValue> {
    return {
      ...classSession,
      dateTime: classSession.dateTime ? classSession.dateTime.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
