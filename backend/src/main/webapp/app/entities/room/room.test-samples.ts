import { IRoom, NewRoom } from './room.model';

export const sampleWithRequiredData: IRoom = {
  id: 25051,
  name: 'wire',
  capacity: 19151,
};

export const sampleWithPartialData: IRoom = {
  id: 14785,
  name: 'because',
  capacity: 23269,
};

export const sampleWithFullData: IRoom = {
  id: 10102,
  name: 'roughly brr opera',
  capacity: 15915,
};

export const sampleWithNewData: NewRoom = {
  name: 'cellar',
  capacity: 32219,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
