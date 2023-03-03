import dayjs from 'dayjs/esm';

import { IReporte, NewReporte } from './reporte.model';

export const sampleWithRequiredData: IReporte = {
  id: 83587,
  descripcion: '../fake-data/blob/hipster.txt',
  fecha: dayjs('2023-03-01T00:38'),
};

export const sampleWithPartialData: IReporte = {
  id: 86493,
  descripcion: '../fake-data/blob/hipster.txt',
  fecha: dayjs('2023-03-01T22:05'),
};

export const sampleWithFullData: IReporte = {
  id: 74989,
  descripcion: '../fake-data/blob/hipster.txt',
  fecha: dayjs('2023-03-01T23:01'),
};

export const sampleWithNewData: NewReporte = {
  descripcion: '../fake-data/blob/hipster.txt',
  fecha: dayjs('2023-03-01T03:41'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
