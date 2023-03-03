import dayjs from 'dayjs/esm';

export interface ICiudad {
  id: number;
  nombre?: string | null;
  createdBy?: string | null;
  createdDate?: dayjs.Dayjs | null;
  lastModifiedBy?: string | null;
  lastModifiedDate?: dayjs.Dayjs | null;
}

export type NewCiudad = Omit<ICiudad, 'id'> & { id: null };
