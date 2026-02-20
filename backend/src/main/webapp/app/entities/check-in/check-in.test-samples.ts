import dayjs from 'dayjs/esm';

import { ICheckIn, NewCheckIn } from './check-in.model';

export const sampleWithRequiredData: ICheckIn = {
  id: 24782,
  checkInTime: dayjs('2026-02-19T16:13'),
};

export const sampleWithPartialData: ICheckIn = {
  id: 10394,
  checkInTime: dayjs('2026-02-20T07:47'),
};

export const sampleWithFullData: ICheckIn = {
  id: 22689,
  checkInTime: dayjs('2026-02-20T06:53'),
};

export const sampleWithNewData: NewCheckIn = {
  checkInTime: dayjs('2026-02-19T16:05'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
