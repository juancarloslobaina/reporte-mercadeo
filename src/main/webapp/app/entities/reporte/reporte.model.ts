import dayjs from 'dayjs/esm';
import { IDoctor } from 'app/entities/doctor/doctor.model';
import { ICentroMedico } from 'app/entities/centro-medico/centro-medico.model';

export interface IReporte {
  id: number;
  descripcion?: string | null;
  fecha?: dayjs.Dayjs | null;
  doctor?: Pick<IDoctor, 'id' | 'nombreDoctor'> | null;
  centroMedico?: Pick<ICentroMedico, 'id' | 'nombreCentroMedico'> | null;
}

export type NewReporte = Omit<IReporte, 'id'> & { id: null };
