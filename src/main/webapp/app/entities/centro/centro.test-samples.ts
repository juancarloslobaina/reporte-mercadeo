import { ICentro, NewCentro } from './centro.model';

export const sampleWithRequiredData: ICentro = {
  id: 39242,
  nombre: 'Sorprendente',
};

export const sampleWithPartialData: ICentro = {
  id: 22089,
  nombre: 'back-end',
};

export const sampleWithFullData: ICentro = {
  id: 28495,
  nombre: 'Andalucía',
};

export const sampleWithNewData: NewCentro = {
  nombre: 'SMS Marroquinería',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
