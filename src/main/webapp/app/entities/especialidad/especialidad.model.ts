export interface IEspecialidad {
  id: number;
  nombreEspecialidad?: string | null;
}

export type NewEspecialidad = Omit<IEspecialidad, 'id'> & { id: null };
