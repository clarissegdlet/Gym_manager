import { IMember, NewMember } from './member.model';

export const sampleWithRequiredData: IMember = {
  id: 26797,
  name: 'huzzah',
  email: 'Brandy67@yahoo.com',
  active: false,
};

export const sampleWithPartialData: IMember = {
  id: 16721,
  name: 'er',
  email: 'Blaze.Farrell@hotmail.com',
  active: true,
};

export const sampleWithFullData: IMember = {
  id: 30531,
  name: 'triangular first',
  email: 'Jefferey32@yahoo.com',
  phone: '287.337.4483 x94676',
  active: false,
};

export const sampleWithNewData: NewMember = {
  name: 'mystify',
  email: 'Abagail.Dicki@hotmail.com',
  active: false,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
