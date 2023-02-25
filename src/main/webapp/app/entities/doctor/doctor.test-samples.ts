import { IDoctor, NewDoctor } from './doctor.model';

export const sampleWithRequiredData: IDoctor = {
  id: 47308,
  nombreDoctor: 'Subida brand',
  correoCorporativo: 'rT@Ah+.22>',
  telefonoPersonal: 'facilitate Convertible',
};

export const sampleWithPartialData: IDoctor = {
  id: 62820,
  nombreDoctor: 'Parafarmacia',
  correoCorporativo: '"2.`>@^gnN{#.}8',
  telefonoPersonal: 'Granito mobile application',
  telefonoCorporativo: 'Mascotas Senda Negro',
};

export const sampleWithFullData: IDoctor = {
  id: 87634,
  nombreDoctor: 'Negro',
  correoPersonal: "a^-|a'@bz/.6z&g*Z",
  correoCorporativo: 'R5Mr&}@vN.NeX',
  telefonoPersonal: 'port Práctico Tenge',
  telefonoCorporativo: 'Guapa',
};

export const sampleWithNewData: NewDoctor = {
  nombreDoctor: 'Parafarmacia',
  correoCorporativo: '6us@KM.8.`sv',
  telefonoPersonal: 'generate monetize',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
