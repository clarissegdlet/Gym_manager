import dayjs from 'dayjs/esm';
import { IMember } from 'app/entities/member/member.model';
import { MembershipStatus } from 'app/entities/enumerations/membership-status.model';

export interface IMembership {
  id: number;
  type?: string | null;
  startDate?: dayjs.Dayjs | null;
  endDate?: dayjs.Dayjs | null;
  status?: keyof typeof MembershipStatus | null;
  member?: Pick<IMember, 'id'> | null;
}

export type NewMembership = Omit<IMembership, 'id'> & { id: null };
