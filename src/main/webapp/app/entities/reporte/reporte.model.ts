import dayjs from 'dayjs/esm';
import { ICentro } from 'app/entities/centro/centro.model';
import { IDoctor } from 'app/entities/doctor/doctor.model';

export interface IReporte {
  id: number;
  descripcion?: string | null;
  fecha?: dayjs.Dayjs | null;
  centro?: Pick<ICentro, 'id' | 'nombre'> | null;
  doctor?: Pick<IDoctor, 'id' | 'nombre'> | null;
}

export type NewReporte = Omit<IReporte, 'id'> & { id: null };
