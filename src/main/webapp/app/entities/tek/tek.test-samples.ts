import { ITek, NewTek } from './tek.model';

export const sampleWithRequiredData: ITek = {
  id: 12816,
  title: 'impactful California',
};

export const sampleWithPartialData: ITek = {
  id: 50922,
  title: 'Architect',
  description: '../fake-data/blob/hipster.txt',
};

export const sampleWithFullData: ITek = {
  id: 59403,
  title: 'Avon Ohio transform',
  description: '../fake-data/blob/hipster.txt',
};

export const sampleWithNewData: NewTek = {
  title: 'Buckinghamshire Granite Malaysia',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
