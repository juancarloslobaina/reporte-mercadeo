import dayjs from 'dayjs/esm';

import { ICiudad, NewCiudad } from './ciudad.model';

export const sampleWithRequiredData: ICiudad = {
  id: 49638,
  nombre: 'Total Sorprendente',
};

export const sampleWithPartialData: ICiudad = {
  id: 19181,
  nombre: 'vertical Electrónica Algodón',
  createdBy: 'Avenida Somali Rial',
  createdDate: dayjs('2023-03-01T18:44'),
  lastModifiedBy: 'Loan TCP',
};

export const sampleWithFullData: ICiudad = {
  id: 67167,
  nombre: 'Moda wireless Respuesta',
  createdBy: 'pixel Pescado Consultor',
  createdDate: dayjs('2023-03-01T22:18'),
  lastModifiedBy: 'Asistente',
  lastModifiedDate: dayjs('2023-03-01T16:05'),
};

export const sampleWithNewData: NewCiudad = {
  nombre: 'Librería',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
