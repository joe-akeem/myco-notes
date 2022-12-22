import { IImage, NewImage } from './image.model';

export const sampleWithRequiredData: IImage = {
  id: 41829,
};

export const sampleWithPartialData: IImage = {
  id: 83327,
  image: '../fake-data/blob/hipster.png',
  imageContentType: 'unknown',
};

export const sampleWithFullData: IImage = {
  id: 84334,
  title: 'driver Toys value-added',
  description: '../fake-data/blob/hipster.txt',
  image: '../fake-data/blob/hipster.png',
  imageContentType: 'unknown',
};

export const sampleWithNewData: NewImage = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
