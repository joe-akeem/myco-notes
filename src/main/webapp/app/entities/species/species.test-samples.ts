import { ISpecies, NewSpecies } from './species.model';

export const sampleWithRequiredData: ISpecies = {
  id: 5813,
  name: 'Movies',
};

export const sampleWithPartialData: ISpecies = {
  id: 88301,
  name: 'Polarised',
};

export const sampleWithFullData: ISpecies = {
  id: 56535,
  name: 'Taiwan',
  commonName: 'Springs incubate',
  description: '../fake-data/blob/hipster.txt',
};

export const sampleWithNewData: NewSpecies = {
  name: 'Granite mesh',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
