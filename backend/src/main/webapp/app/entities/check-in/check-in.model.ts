import dayjs from 'dayjs/esm';
import { IMember } from 'app/entities/member/member.model';

export interface ICheckIn {
  id: number;
  checkInTime?: dayjs.Dayjs | null;
  member?: Pick<IMember, 'id'> | null;
}

export type NewCheckIn = Omit<ICheckIn, 'id'> & { id: null };
