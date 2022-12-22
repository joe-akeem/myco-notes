import dayjs from 'dayjs/esm';
import { IExperiment } from 'app/entities/experiment/experiment.model';

export interface IObservation {
  id: number;
  observationDate?: dayjs.Dayjs | null;
  title?: string | null;
  description?: string | null;
  experiment?: Pick<IExperiment, 'id' | 'title'> | null;
}

export type NewObservation = Omit<IObservation, 'id'> & { id: null };
