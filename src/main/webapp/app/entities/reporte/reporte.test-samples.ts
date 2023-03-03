import dayjs from 'dayjs/esm';

import { IReporte, NewReporte } from './reporte.model';

export const sampleWithRequiredData: IReporte = {
  id: 83587,
  descripcion: '../fake-data/blob/hipster.txt',
  fecha: dayjs('2023-03-01T00:38'),
};

export const sampleWithPartialData: IReporte = {
  id: 83232,
  descripcion: '../fake-data/blob/hipster.txt',
  fecha: dayjs('2023-03-01T16:23'),
  createdBy: 'Savings Métricas Gerente',
  lastModifiedBy: 'regional Asimilado Refinado',
};

export const sampleWithFullData: IReporte = {
  id: 24734,
  descripcion: '../fake-data/blob/hipster.txt',
  fecha: dayjs('2023-03-01T00:34'),
  createdBy: 'Genérico Marroquinería deploy',
  createdDate: dayjs('2023-03-01T07:27'),
  lastModifiedBy: 'Programa Música deposit',
  lastModifiedDate: dayjs('2023-03-01T11:26'),
};

export const sampleWithNewData: NewReporte = {
  descripcion: '../fake-data/blob/hipster.txt',
  fecha: dayjs('2023-03-01T19:21'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
