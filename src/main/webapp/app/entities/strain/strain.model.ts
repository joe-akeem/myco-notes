import dayjs from 'dayjs/esm';
import { ISpecies } from 'app/entities/species/species.model';
import { IExperiment } from 'app/entities/experiment/experiment.model';

export interface IStrain {
  id: number;
  name?: string | null;
  description?: string | null;
  isolatedAt?: dayjs.Dayjs | null;
  species?: Pick<ISpecies, 'id' | 'name' | 'commonName'> | null;
  origin?: Pick<IExperiment, 'id' | 'title'> | null;
  experiments?: Pick<IExperiment, 'id' | 'title'>[] | null;
}

export type NewStrain = Omit<IStrain, 'id'> & { id: null };
