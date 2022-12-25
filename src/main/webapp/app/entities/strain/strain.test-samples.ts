import dayjs from 'dayjs/esm';

import { IStrain, NewStrain } from './strain.model';

export const sampleWithRequiredData: IStrain = {
  id: 1481,
  name: 'Maryland',
  isolatedAt: dayjs('2022-12-21'),
};

export const sampleWithPartialData: IStrain = {
  id: 92801,
  name: 'TCP Granite',
  description: '../fake-data/blob/hipster.txt',
  isolatedAt: dayjs('2022-12-21'),
  fruiting: false,
};

export const sampleWithFullData: IStrain = {
  id: 63326,
  name: 'throughput Account',
  description: '../fake-data/blob/hipster.txt',
  isolatedAt: dayjs('2022-12-22'),
  fruiting: false,
};

export const sampleWithNewData: NewStrain = {
  name: 'transmitting',
  isolatedAt: dayjs('2022-12-21'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
