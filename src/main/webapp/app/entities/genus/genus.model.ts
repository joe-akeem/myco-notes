export interface IGenus {
  id: number;
  name?: string | null;
  commonName?: string | null;
  description?: string | null;
}

export type NewGenus = Omit<IGenus, 'id'> & { id: null };
