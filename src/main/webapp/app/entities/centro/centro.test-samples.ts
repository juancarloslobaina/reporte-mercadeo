import dayjs from 'dayjs/esm';

import { ICentro, NewCentro } from './centro.model';

export const sampleWithRequiredData: ICentro = {
  id: 39242,
  nombre: 'Sorprendente',
};

export const sampleWithPartialData: ICentro = {
  id: 32799,
  nombre: 'Granito',
  lastModifiedBy: 'Cambridgeshire implement',
};

export const sampleWithFullData: ICentro = {
  id: 59557,
  nombre: 'payment éxito',
  createdBy: 'Hormigon Mesa Respuesta',
  createdDate: dayjs('2023-03-01T13:17'),
  lastModifiedBy: 'Amarillo Baleares Salud',
  lastModifiedDate: dayjs('2023-03-01T10:24'),
};

export const sampleWithNewData: NewCentro = {
  nombre: 'unleash',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
