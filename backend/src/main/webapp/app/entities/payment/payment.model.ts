import dayjs from 'dayjs/esm';
import { IMembership } from 'app/entities/membership/membership.model';

export interface IPayment {
  id: number;
  amount?: number | null;
  paymentDate?: dayjs.Dayjs | null;
  method?: string | null;
  membership?: Pick<IMembership, 'id'> | null;
}

export type NewPayment = Omit<IPayment, 'id'> & { id: null };
