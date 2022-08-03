import { ICentroMedico, NewCentroMedico } from './centro-medico.model';

export const sampleWithRequiredData: ICentroMedico = {
  id: 25956,
  nombreCentroMedico: 'transmit',
};

export const sampleWithPartialData: ICentroMedico = {
  id: 84372,
  nombreCentroMedico: 'Sabroso Hecho Hormigon',
};

export const sampleWithFullData: ICentroMedico = {
  id: 9338,
  nombreCentroMedico: 'vertical',
};

export const sampleWithNewData: NewCentroMedico = {
  nombreCentroMedico: 'Ergonómico productize',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
