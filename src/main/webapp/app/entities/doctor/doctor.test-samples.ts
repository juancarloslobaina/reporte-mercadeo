import dayjs from 'dayjs/esm';

import { IDoctor, NewDoctor } from './doctor.model';

export const sampleWithRequiredData: IDoctor = {
  id: 47308,
  nombre: 'Subida brand',
  correoCorporativo: 'rT@Ah+.22>',
  telefonoCorporativo: 'facilitate Convertible',
};

export const sampleWithPartialData: IDoctor = {
  id: 24666,
  nombre: 'Electrónica Dollar payment',
  correoCorporativo: '{#<@8n-TKm.~eh2e!',
  telefonoPersonal: 'Algodón Dominican',
  telefonoCorporativo: 'HTTP función Pakistan',
  createdBy: 'Taka Palladium Gerente',
  lastModifiedDate: dayjs('2023-03-01T22:12'),
};

export const sampleWithFullData: IDoctor = {
  id: 74558,
  nombre: 'Loan',
  correoPersonal: 'Mr@}.vN',
  correoCorporativo: "NeX@d9*'L.]",
  telefonoPersonal: 'Tenge',
  telefonoCorporativo: 'Guapa',
  createdBy: 'Parafarmacia',
  createdDate: dayjs('2023-03-01T12:06'),
  lastModifiedBy: 'Humano',
  lastModifiedDate: dayjs('2023-03-01T12:55'),
};

export const sampleWithNewData: NewDoctor = {
  nombre: 'Euro BEAC',
  correoCorporativo: 'hUu@>~#.J]?Hy',
  telefonoCorporativo: 'withdrawal multimedia',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
