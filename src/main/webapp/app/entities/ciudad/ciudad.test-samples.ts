import { ICiudad, NewCiudad } from './ciudad.model';

export const sampleWithRequiredData: ICiudad = {
  id: 49638,
  nombreCiudad: 'Total Sorprendente',
};

export const sampleWithPartialData: ICiudad = {
  id: 57553,
  nombreCiudad: 'withdrawal Identidad',
};

export const sampleWithFullData: ICiudad = {
  id: 44049,
  nombreCiudad: 'Queso',
};

export const sampleWithNewData: NewCiudad = {
  nombreCiudad: 'Prolongación online wireless',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
