import { IInstruction, NewInstruction } from './instruction.model';

export const sampleWithRequiredData: IInstruction = {
  id: 82100,
  title: 'responsive orchestrate',
};

export const sampleWithPartialData: IInstruction = {
  id: 10505,
  title: 'tan Keyboard',
};

export const sampleWithFullData: IInstruction = {
  id: 32412,
  title: 'group Ways product',
  description: '../fake-data/blob/hipster.txt',
};

export const sampleWithNewData: NewInstruction = {
  title: 'exploit Cotton',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
