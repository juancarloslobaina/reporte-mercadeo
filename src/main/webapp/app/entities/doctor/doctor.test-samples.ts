import { IDoctor, NewDoctor } from './doctor.model';

export const sampleWithRequiredData: IDoctor = {
  id: 47308,
  nombreDoctor: 'Subida brand',
};

export const sampleWithPartialData: IDoctor = {
  id: 33543,
  nombreDoctor: 'Plástico Negro facilitate',
  correoCorporativo: 'Verde Borders Parafarmacia',
  telefonoPersonal: 'Electrónica Dollar payment',
};

export const sampleWithFullData: IDoctor = {
  id: 47714,
  nombreDoctor: 'Joyería Fácil Irak',
  correoPersonal: 'Productor Verde infrastructures',
  correoCorporativo: 'Dominican Contabilidad Negro',
  telefonoPersonal: 'Pakistan Desarrollador cross-platform',
  telefonoCorporativo: 'Gerente',
};

export const sampleWithNewData: NewDoctor = {
  nombreDoctor: 'bypass',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
