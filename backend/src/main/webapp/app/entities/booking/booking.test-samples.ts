import dayjs from 'dayjs/esm';

import { IBooking, NewBooking } from './booking.model';

export const sampleWithRequiredData: IBooking = {
  id: 4955,
  bookingStatus: 'CONFIRMED',
  bookingTime: dayjs('2026-02-19T22:06'),
};

export const sampleWithPartialData: IBooking = {
  id: 76,
  bookingStatus: 'CANCELLED',
  bookingTime: dayjs('2026-02-19T12:38'),
};

export const sampleWithFullData: IBooking = {
  id: 28482,
  bookingStatus: 'CANCELLED',
  bookingTime: dayjs('2026-02-20T03:28'),
};

export const sampleWithNewData: NewBooking = {
  bookingStatus: 'CONFIRMED',
  bookingTime: dayjs('2026-02-19T19:19'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
