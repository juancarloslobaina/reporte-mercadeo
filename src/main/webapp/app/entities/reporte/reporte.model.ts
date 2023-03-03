import dayjs from 'dayjs/esm';
import { ICentro } from 'app/entities/centro/centro.model';
import { IDoctor } from 'app/entities/doctor/doctor.model';
import { IUser } from 'app/entities/user/user.model';

export interface IReporte {
  id: number;
  descripcion?: string | null;
  fecha?: dayjs.Dayjs | null;
  centro?: Pick<ICentro, 'id' | 'nombre'> | null;
  doctor?: Pick<IDoctor, 'id' | 'nombre'> | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewReporte = Omit<IReporte, 'id'> & { id: null };
