import { IEspecialidad, NewEspecialidad } from './especialidad.model';

export const sampleWithRequiredData: IEspecialidad = {
  id: 39622,
  descripcion: 'Rioja Hecho Jamaica',
};

export const sampleWithPartialData: IEspecialidad = {
  id: 96568,
  descripcion: 'Berkshire Bedfordshire',
};

export const sampleWithFullData: IEspecialidad = {
  id: 93997,
  descripcion: 'Teclado',
};

export const sampleWithNewData: NewEspecialidad = {
  descripcion: 'adaptador',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
