import { IGenus, NewGenus } from './genus.model';

export const sampleWithRequiredData: IGenus = {
  id: 61816,
  name: 'Representative',
};

export const sampleWithPartialData: IGenus = {
  id: 39024,
  name: 'Account Incredible',
  commonName: 'Savings Cambridgeshire Montana',
};

export const sampleWithFullData: IGenus = {
  id: 6556,
  name: 'Practical',
  commonName: 'Afghanistan',
  description: '../fake-data/blob/hipster.txt',
};

export const sampleWithNewData: NewGenus = {
  name: 'system-worthy',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
