import dayjs from 'dayjs/esm';
import { ITek } from 'app/entities/tek/tek.model';
import { IUser } from 'app/entities/user/user.model';
import { IStrain } from 'app/entities/strain/strain.model';
import { Row } from 'angular-google-charts';

export interface IExperiment {
  id: number;
  title?: string | null;
  notes?: string | null;
  conductedAt?: dayjs.Dayjs | null;
  tek?: Pick<ITek, 'id' | 'title'> | null;
  conductedBy?: Pick<IUser, 'id' | 'login'> | null;
  involvedStrains?: Pick<IStrain, 'id' | 'name' | 'species'>[] | null;
  precedingExperiments?: Pick<IExperiment, 'id' | 'title'>[] | null;
  followupExperiments?: Pick<IExperiment, 'id' | 'title'>[] | null;
}

export type NewExperiment = Omit<IExperiment, 'id'> & { id: null };

export interface GanttRow extends Row {
  taskId: string;
  taskName: string;
  resource: string;
  startDate: Date;
  endDate: Date;
  duration: number;
  percentComplete: number;
  dependencies: string;
}
