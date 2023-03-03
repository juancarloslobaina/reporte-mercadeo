export interface ICiudad {
  id: number;
  nombre?: string | null;
}

export type NewCiudad = Omit<ICiudad, 'id'> & { id: null };
