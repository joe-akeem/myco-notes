export interface ITek {
  id: number;
  title?: string | null;
  description?: string | null;
}

export type NewTek = Omit<ITek, 'id'> & { id: null };
