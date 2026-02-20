import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IMember, NewMember } from '../member.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IMember for edit and NewMemberFormGroupInput for create.
 */
type MemberFormGroupInput = IMember | PartialWithRequiredKeyOf<NewMember>;

type MemberFormDefaults = Pick<NewMember, 'id' | 'active'>;

type MemberFormGroupContent = {
  id: FormControl<IMember['id'] | NewMember['id']>;
  name: FormControl<IMember['name']>;
  email: FormControl<IMember['email']>;
  phone: FormControl<IMember['phone']>;
  active: FormControl<IMember['active']>;
};

export type MemberFormGroup = FormGroup<MemberFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class MemberFormService {
  createMemberFormGroup(member: MemberFormGroupInput = { id: null }): MemberFormGroup {
    const memberRawValue = {
      ...this.getFormDefaults(),
      ...member,
    };
    return new FormGroup<MemberFormGroupContent>({
      id: new FormControl(
        { value: memberRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(memberRawValue.name, {
        validators: [Validators.required],
      }),
      email: new FormControl(memberRawValue.email, {
        validators: [Validators.required],
      }),
      phone: new FormControl(memberRawValue.phone),
      active: new FormControl(memberRawValue.active, {
        validators: [Validators.required],
      }),
    });
  }

  getMember(form: MemberFormGroup): IMember | NewMember {
    return form.getRawValue() as IMember | NewMember;
  }

  resetForm(form: MemberFormGroup, member: MemberFormGroupInput): void {
    const memberRawValue = { ...this.getFormDefaults(), ...member };
    form.reset(
      {
        ...memberRawValue,
        id: { value: memberRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): MemberFormDefaults {
    return {
      id: null,
      active: false,
    };
  }
}
