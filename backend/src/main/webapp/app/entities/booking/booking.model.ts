import dayjs from 'dayjs/esm';
import { IMember } from 'app/entities/member/member.model';
import { IClassSession } from 'app/entities/class-session/class-session.model';
import { BookingStatus } from 'app/entities/enumerations/booking-status.model';

export interface IBooking {
  id: number;
  bookingStatus?: keyof typeof BookingStatus | null;
  bookingTime?: dayjs.Dayjs | null;
  member?: Pick<IMember, 'id'> | null;
  classSession?: Pick<IClassSession, 'id'> | null;
}

export type NewBooking = Omit<IBooking, 'id'> & { id: null };
