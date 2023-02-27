import { ICiudad } from 'app/entities/ciudad/ciudad.model';

export interface ICentro {
  id: number;
  nombre?: string | null;
  ciudad?: Pick<ICiudad, 'id' | 'nombre'> | null;
}

export type NewCentro = Omit<ICentro, 'id'> & { id: null };
