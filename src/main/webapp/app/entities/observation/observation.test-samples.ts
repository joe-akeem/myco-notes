import dayjs from 'dayjs/esm';

import { IObservation, NewObservation } from './observation.model';

export const sampleWithRequiredData: IObservation = {
  id: 84073,
  observationDate: dayjs('2022-12-21'),
  title: 'microchip methodical',
};

export const sampleWithPartialData: IObservation = {
  id: 49002,
  observationDate: dayjs('2022-12-22'),
  title: 'impactful',
  description: '../fake-data/blob/hipster.txt',
};

export const sampleWithFullData: IObservation = {
  id: 46129,
  observationDate: dayjs('2022-12-21'),
  title: 'lavender',
  description: '../fake-data/blob/hipster.txt',
};

export const sampleWithNewData: NewObservation = {
  observationDate: dayjs('2022-12-21'),
  title: 'panel Loan Rwanda',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
