import { IEspecialidad } from 'app/entities/especialidad/especialidad.model';

export interface IDoctor {
  id: number;
  nombre?: string | null;
  correoPersonal?: string | null;
  correoCorporativo?: string | null;
  telefonoPersonal?: string | null;
  telefonoCorporativo?: string | null;
  especialidad?: Pick<IEspecialidad, 'id' | 'descripcion'> | null;
}

export type NewDoctor = Omit<IDoctor, 'id'> & { id: null };
