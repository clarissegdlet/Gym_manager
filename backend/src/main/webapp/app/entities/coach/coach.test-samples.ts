import { ICoach, NewCoach } from './coach.model';

export const sampleWithRequiredData: ICoach = {
  id: 9527,
  name: 'classic while smart',
};

export const sampleWithPartialData: ICoach = {
  id: 13939,
  name: 'coin',
  specialty: 'a',
};

export const sampleWithFullData: ICoach = {
  id: 2690,
  name: 'married',
  specialty: 'physically notwithstanding indeed',
};

export const sampleWithNewData: NewCoach = {
  name: 'ick',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
