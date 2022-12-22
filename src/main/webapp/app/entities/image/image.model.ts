import { IObservation } from 'app/entities/observation/observation.model';
import { IStrain } from 'app/entities/strain/strain.model';
import { ITek } from 'app/entities/tek/tek.model';

export interface IImage {
  id: number;
  title?: string | null;
  description?: string | null;
  image?: string | null;
  imageContentType?: string | null;
  observation?: Pick<IObservation, 'id' | 'title'> | null;
  strain?: Pick<IStrain, 'id' | 'name'> | null;
  tek?: Pick<ITek, 'id' | 'title'> | null;
}

export type NewImage = Omit<IImage, 'id'> & { id: null };
