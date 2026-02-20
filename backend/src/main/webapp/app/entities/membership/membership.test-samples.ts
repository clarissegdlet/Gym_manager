import dayjs from 'dayjs/esm';

import { IMembership, NewMembership } from './membership.model';

export const sampleWithRequiredData: IMembership = {
  id: 4634,
  type: 'total',
  startDate: dayjs('2026-02-20T01:34'),
  endDate: dayjs('2026-02-20T01:03'),
  status: 'EXPIRED',
};

export const sampleWithPartialData: IMembership = {
  id: 18196,
  type: 'cautious',
  startDate: dayjs('2026-02-20T03:56'),
  endDate: dayjs('2026-02-20T10:17'),
  status: 'ACTIVE',
};

export const sampleWithFullData: IMembership = {
  id: 14222,
  type: 'shaft yummy',
  startDate: dayjs('2026-02-20T10:15'),
  endDate: dayjs('2026-02-20T05:17'),
  status: 'ACTIVE',
};

export const sampleWithNewData: NewMembership = {
  type: 'scoop whose whether',
  startDate: dayjs('2026-02-19T10:41'),
  endDate: dayjs('2026-02-19T16:49'),
  status: 'SUSPENDED',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
