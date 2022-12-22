import dayjs from 'dayjs/esm';
import { ITek } from 'app/entities/tek/tek.model';
import { IUser } from 'app/entities/user/user.model';
import { IStrain } from 'app/entities/strain/strain.model';

export interface IExperiment {
  id: number;
  title?: string | null;
  notes?: string | null;
  conductedAt?: dayjs.Dayjs | null;
  instructions?: Pick<ITek, 'id' | 'title'> | null;
  conductedBy?: Pick<IUser, 'id' | 'login'> | null;
  involvedStrains?: Pick<IStrain, 'id' | 'name'>[] | null;
  precedingExperiments?: Pick<IExperiment, 'id' | 'title'>[] | null;
  followupExperiments?: Pick<IExperiment, 'id' | 'title'>[] | null;
}

export type NewExperiment = Omit<IExperiment, 'id'> & { id: null };
