export interface ICiudad {
  id: number;
  nombreCiudad?: string | null;
}

export type NewCiudad = Omit<ICiudad, 'id'> & { id: null };
