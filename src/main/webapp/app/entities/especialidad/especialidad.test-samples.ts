import { IEspecialidad, NewEspecialidad } from './especialidad.model';

export const sampleWithRequiredData: IEspecialidad = {
  id: 39622,
  nombreEspecialidad: 'Rioja Hecho Jamaica',
};

export const sampleWithPartialData: IEspecialidad = {
  id: 96568,
  nombreEspecialidad: 'Berkshire Bedfordshire',
};

export const sampleWithFullData: IEspecialidad = {
  id: 93997,
  nombreEspecialidad: 'Teclado',
};

export const sampleWithNewData: NewEspecialidad = {
  nombreEspecialidad: 'adaptador',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
