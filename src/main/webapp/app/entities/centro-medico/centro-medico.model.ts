import { ICiudad } from 'app/entities/ciudad/ciudad.model';

export interface ICentroMedico {
  id: number;
  nombreCentroMedico?: string | null;
  ciudad?: Pick<ICiudad, 'id' | 'nombreCiudad'> | null;
}

export type NewCentroMedico = Omit<ICentroMedico, 'id'> & { id: null };
