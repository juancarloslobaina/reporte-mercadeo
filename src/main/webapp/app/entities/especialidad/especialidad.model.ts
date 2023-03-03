import dayjs from 'dayjs/esm';

export interface IEspecialidad {
  id: number;
  descripcion?: string | null;
  createdBy?: string | null;
  createdDate?: dayjs.Dayjs | null;
  lastModifiedBy?: string | null;
  lastModifiedDate?: dayjs.Dayjs | null;
}

export type NewEspecialidad = Omit<IEspecialidad, 'id'> & { id: null };
