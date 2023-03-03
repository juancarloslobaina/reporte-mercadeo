export interface IEspecialidad {
  id: number;
  descripcion?: string | null;
}

export type NewEspecialidad = Omit<IEspecialidad, 'id'> & { id: null };
