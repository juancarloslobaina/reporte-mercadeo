import dayjs from 'dayjs/esm';
import { ICiudad } from 'app/entities/ciudad/ciudad.model';
import { IUser } from 'app/entities/user/user.model';

export interface ICentro {
  id: number;
  nombre?: string | null;
  createdBy?: string | null;
  createdDate?: dayjs.Dayjs | null;
  lastModifiedBy?: string | null;
  lastModifiedDate?: dayjs.Dayjs | null;
  ciudad?: Pick<ICiudad, 'id' | 'nombre'> | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewCentro = Omit<ICentro, 'id'> & { id: null };
