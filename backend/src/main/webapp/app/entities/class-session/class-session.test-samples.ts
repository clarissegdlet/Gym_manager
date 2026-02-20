import dayjs from 'dayjs/esm';

import { IClassSession, NewClassSession } from './class-session.model';

export const sampleWithRequiredData: IClassSession = {
  id: 29713,
  title: 'reproachfully',
  dateTime: dayjs('2026-02-20T03:10'),
  capacity: 3852,
  status: 'CANCELLED',
};

export const sampleWithPartialData: IClassSession = {
  id: 16415,
  title: 'rationalize',
  dateTime: dayjs('2026-02-20T04:55'),
  capacity: 16879,
  status: 'COMPLETED',
};

export const sampleWithFullData: IClassSession = {
  id: 1615,
  title: 'characterization celsius',
  dateTime: dayjs('2026-02-19T14:52'),
  capacity: 20361,
  status: 'PLANNED',
};

export const sampleWithNewData: NewClassSession = {
  title: 'ick boo',
  dateTime: dayjs('2026-02-19T17:49'),
  capacity: 3716,
  status: 'CANCELLED',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
