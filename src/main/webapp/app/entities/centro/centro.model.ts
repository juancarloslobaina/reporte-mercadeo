import { ICiudad } from 'app/entities/ciudad/ciudad.model';
import { IUser } from 'app/entities/user/user.model';

export interface ICentro {
  id: number;
  nombre?: string | null;
  ciudad?: Pick<ICiudad, 'id' | 'nombre'> | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewCentro = Omit<ICentro, 'id'> & { id: null };
