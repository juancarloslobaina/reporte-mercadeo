import dayjs from 'dayjs/esm';

import { IEspecialidad, NewEspecialidad } from './especialidad.model';

export const sampleWithRequiredData: IEspecialidad = {
  id: 39622,
  descripcion: 'Rioja Hecho Jamaica',
};

export const sampleWithPartialData: IEspecialidad = {
  id: 51359,
  descripcion: 'Mesa Electrónica',
  createdBy: 'definición',
};

export const sampleWithFullData: IEspecialidad = {
  id: 44309,
  descripcion: 'Savings',
  createdBy: 'invoice Metal Timor-Leste',
  createdDate: dayjs('2023-03-01T03:17'),
  lastModifiedBy: 'Algodón Genérico back-end',
  lastModifiedDate: dayjs('2023-03-01T00:36'),
};

export const sampleWithNewData: NewEspecialidad = {
  descripcion: 'Coordinador Rústico',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
