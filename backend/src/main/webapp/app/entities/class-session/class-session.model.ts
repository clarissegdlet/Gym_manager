import dayjs from 'dayjs/esm';
import { ClassStatus } from 'app/entities/enumerations/class-status.model';

export interface IClassSession {
  id: number;
  title?: string | null;
  dateTime?: dayjs.Dayjs | null;
  capacity?: number | null;
  status?: keyof typeof ClassStatus | null;
}

export type NewClassSession = Omit<IClassSession, 'id'> & { id: null };
