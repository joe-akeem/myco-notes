import dayjs from 'dayjs/esm';

import { IExperiment, NewExperiment } from './experiment.model';

export const sampleWithRequiredData: IExperiment = {
  id: 60870,
  title: 'Fantastic reboot',
  conductedAt: dayjs('2022-12-22'),
};

export const sampleWithPartialData: IExperiment = {
  id: 93207,
  title: 'blue Investor',
  notes: '../fake-data/blob/hipster.txt',
  conductedAt: dayjs('2022-12-21'),
};

export const sampleWithFullData: IExperiment = {
  id: 41447,
  title: 'extend',
  notes: '../fake-data/blob/hipster.txt',
  conductedAt: dayjs('2022-12-21'),
};

export const sampleWithNewData: NewExperiment = {
  title: 'Ohio Buckinghamshire cyan',
  conductedAt: dayjs('2022-12-22'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
