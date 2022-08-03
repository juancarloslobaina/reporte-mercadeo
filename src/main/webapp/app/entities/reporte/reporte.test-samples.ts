import dayjs from 'dayjs/esm';

import { IReporte, NewReporte } from './reporte.model';

export const sampleWithRequiredData: IReporte = {
  id: 83587,
  descripcion: '../fake-data/blob/hipster.txt',
  fecha: dayjs('2022-08-01T11:15'),
};

export const sampleWithPartialData: IReporte = {
  id: 86493,
  descripcion: '../fake-data/blob/hipster.txt',
  fecha: dayjs('2022-08-02T08:43'),
};

export const sampleWithFullData: IReporte = {
  id: 74989,
  descripcion: '../fake-data/blob/hipster.txt',
  fecha: dayjs('2022-08-02T09:38'),
};

export const sampleWithNewData: NewReporte = {
  descripcion: '../fake-data/blob/hipster.txt',
  fecha: dayjs('2022-08-01T14:19'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
