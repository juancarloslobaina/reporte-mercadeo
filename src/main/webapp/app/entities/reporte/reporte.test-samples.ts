import dayjs from 'dayjs/esm';

import { IReporte, NewReporte } from './reporte.model';

export const sampleWithRequiredData: IReporte = {
  id: 83587,
  descripcion: '../fake-data/blob/hipster.txt',
  fecha: dayjs('2023-02-26T14:07'),
};

export const sampleWithPartialData: IReporte = {
  id: 86493,
  descripcion: '../fake-data/blob/hipster.txt',
  fecha: dayjs('2023-02-27T11:34'),
};

export const sampleWithFullData: IReporte = {
  id: 74989,
  descripcion: '../fake-data/blob/hipster.txt',
  fecha: dayjs('2023-02-27T12:30'),
};

export const sampleWithNewData: NewReporte = {
  descripcion: '../fake-data/blob/hipster.txt',
  fecha: dayjs('2023-02-26T17:10'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
