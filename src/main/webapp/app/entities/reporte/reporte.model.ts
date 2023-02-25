import dayjs from 'dayjs/esm';
import { ICentroMedico } from 'app/entities/centro-medico/centro-medico.model';
import { IDoctor } from 'app/entities/doctor/doctor.model';

export interface IReporte {
  id: number;
  descripcion?: string | null;
  fecha?: dayjs.Dayjs | null;
  centroMedico?: Pick<ICentroMedico, 'id' | 'nombreCentroMedico'> | null;
  doctor?: Pick<IDoctor, 'id' | 'nombreDoctor'> | null;
}

export type NewReporte = Omit<IReporte, 'id'> & { id: null };
