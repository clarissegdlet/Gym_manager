import dayjs from 'dayjs/esm';
import { ICoach } from 'app/entities/coach/coach.model';
import { IRoom } from 'app/entities/room/room.model';
import { ClassStatus } from 'app/entities/enumerations/class-status.model';

export interface IClassSession {
  id: number;
  title?: string | null;
  dateTime?: dayjs.Dayjs | null;
  capacity?: number | null;
  status?: keyof typeof ClassStatus | null;
  coach?: Pick<ICoach, 'id' | 'name'> | null;
  room?: Pick<IRoom, 'id' | 'name'> | null;
}

export type NewClassSession = Omit<IClassSession, 'id'> & { id: null };
