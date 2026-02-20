import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IMembership, NewMembership } from '../membership.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IMembership for edit and NewMembershipFormGroupInput for create.
 */
type MembershipFormGroupInput = IMembership | PartialWithRequiredKeyOf<NewMembership>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IMembership | NewMembership> = Omit<T, 'startDate' | 'endDate'> & {
  startDate?: string | null;
  endDate?: string | null;
};

type MembershipFormRawValue = FormValueOf<IMembership>;

type NewMembershipFormRawValue = FormValueOf<NewMembership>;

type MembershipFormDefaults = Pick<NewMembership, 'id' | 'startDate' | 'endDate'>;

type MembershipFormGroupContent = {
  id: FormControl<MembershipFormRawValue['id'] | NewMembership['id']>;
  type: FormControl<MembershipFormRawValue['type']>;
  startDate: FormControl<MembershipFormRawValue['startDate']>;
  endDate: FormControl<MembershipFormRawValue['endDate']>;
  status: FormControl<MembershipFormRawValue['status']>;
  member: FormControl<MembershipFormRawValue['member']>;
};

export type MembershipFormGroup = FormGroup<MembershipFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class MembershipFormService {
  createMembershipFormGroup(membership: MembershipFormGroupInput = { id: null }): MembershipFormGroup {
    const membershipRawValue = this.convertMembershipToMembershipRawValue({
      ...this.getFormDefaults(),
      ...membership,
    });
    return new FormGroup<MembershipFormGroupContent>({
      id: new FormControl(
        { value: membershipRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      type: new FormControl(membershipRawValue.type, {
        validators: [Validators.required],
      }),
      startDate: new FormControl(membershipRawValue.startDate, {
        validators: [Validators.required],
      }),
      endDate: new FormControl(membershipRawValue.endDate, {
        validators: [Validators.required],
      }),
      status: new FormControl(membershipRawValue.status, {
        validators: [Validators.required],
      }),
      member: new FormControl(membershipRawValue.member, {
        validators: [Validators.required],
      }),
    });
  }

  getMembership(form: MembershipFormGroup): IMembership | NewMembership {
    return this.convertMembershipRawValueToMembership(form.getRawValue() as MembershipFormRawValue | NewMembershipFormRawValue);
  }

  resetForm(form: MembershipFormGroup, membership: MembershipFormGroupInput): void {
    const membershipRawValue = this.convertMembershipToMembershipRawValue({ ...this.getFormDefaults(), ...membership });
    form.reset(
      {
        ...membershipRawValue,
        id: { value: membershipRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): MembershipFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      startDate: currentTime,
      endDate: currentTime,
    };
  }

  private convertMembershipRawValueToMembership(
    rawMembership: MembershipFormRawValue | NewMembershipFormRawValue,
  ): IMembership | NewMembership {
    return {
      ...rawMembership,
      startDate: dayjs(rawMembership.startDate, DATE_TIME_FORMAT),
      endDate: dayjs(rawMembership.endDate, DATE_TIME_FORMAT),
    };
  }

  private convertMembershipToMembershipRawValue(
    membership: IMembership | (Partial<NewMembership> & MembershipFormDefaults),
  ): MembershipFormRawValue | PartialWithRequiredKeyOf<NewMembershipFormRawValue> {
    return {
      ...membership,
      startDate: membership.startDate ? membership.startDate.format(DATE_TIME_FORMAT) : undefined,
      endDate: membership.endDate ? membership.endDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
