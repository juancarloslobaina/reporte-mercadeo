import { ICiudad, NewCiudad } from './ciudad.model';

export const sampleWithRequiredData: ICiudad = {
  id: 49638,
  nombre: 'Total Sorprendente',
};

export const sampleWithPartialData: ICiudad = {
  id: 57553,
  nombre: 'withdrawal Identidad',
};

export const sampleWithFullData: ICiudad = {
  id: 44049,
  nombre: 'Queso',
};

export const sampleWithNewData: NewCiudad = {
  nombre: 'Prolongación online wireless',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
