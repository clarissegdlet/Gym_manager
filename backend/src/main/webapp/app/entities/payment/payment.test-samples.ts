import dayjs from 'dayjs/esm';

import { IPayment, NewPayment } from './payment.model';

export const sampleWithRequiredData: IPayment = {
  id: 4942,
  amount: 18523.62,
  paymentDate: dayjs('2026-02-19T20:12'),
};

export const sampleWithPartialData: IPayment = {
  id: 6955,
  amount: 26734.6,
  paymentDate: dayjs('2026-02-20T00:12'),
};

export const sampleWithFullData: IPayment = {
  id: 28239,
  amount: 11240.4,
  paymentDate: dayjs('2026-02-19T21:12'),
  method: 'ouch out',
};

export const sampleWithNewData: NewPayment = {
  amount: 7354.94,
  paymentDate: dayjs('2026-02-19T13:20'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
