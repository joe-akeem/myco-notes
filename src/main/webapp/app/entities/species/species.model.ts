import { IGenus } from 'app/entities/genus/genus.model';

export interface ISpecies {
  id: number;
  name?: string | null;
  commonName?: string | null;
  description?: string | null;
  genus?: Pick<IGenus, 'id' | 'name'> | null;
}

export type NewSpecies = Omit<ISpecies, 'id'> & { id: null };
