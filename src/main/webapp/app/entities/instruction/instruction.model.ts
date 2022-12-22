import { ITek } from 'app/entities/tek/tek.model';

export interface IInstruction {
  id: number;
  title?: string | null;
  description?: string | null;
  instructionSet?: Pick<ITek, 'id' | 'title'> | null;
}

export type NewInstruction = Omit<IInstruction, 'id'> & { id: null };
