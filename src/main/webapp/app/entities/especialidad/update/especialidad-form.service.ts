import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IEspecialidad, NewEspecialidad } from '../especialidad.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IEspecialidad for edit and NewEspecialidadFormGroupInput for create.
 */
type EspecialidadFormGroupInput = IEspecialidad | PartialWithRequiredKeyOf<NewEspecialidad>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IEspecialidad | NewEspecialidad> = Omit<T, 'createdDate' | 'lastModifiedDate'> & {
  createdDate?: string | null;
  lastModifiedDate?: string | null;
};

type EspecialidadFormRawValue = FormValueOf<IEspecialidad>;

type NewEspecialidadFormRawValue = FormValueOf<NewEspecialidad>;

type EspecialidadFormDefaults = Pick<NewEspecialidad, 'id' | 'createdDate' | 'lastModifiedDate'>;

type EspecialidadFormGroupContent = {
  id: FormControl<EspecialidadFormRawValue['id'] | NewEspecialidad['id']>;
  descripcion: FormControl<EspecialidadFormRawValue['descripcion']>;
  createdBy: FormControl<EspecialidadFormRawValue['createdBy']>;
  createdDate: FormControl<EspecialidadFormRawValue['createdDate']>;
  lastModifiedBy: FormControl<EspecialidadFormRawValue['lastModifiedBy']>;
  lastModifiedDate: FormControl<EspecialidadFormRawValue['lastModifiedDate']>;
};

export type EspecialidadFormGroup = FormGroup<EspecialidadFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class EspecialidadFormService {
  createEspecialidadFormGroup(especialidad: EspecialidadFormGroupInput = { id: null }): EspecialidadFormGroup {
    const especialidadRawValue = this.convertEspecialidadToEspecialidadRawValue({
      ...this.getFormDefaults(),
      ...especialidad,
    });
    return new FormGroup<EspecialidadFormGroupContent>({
      id: new FormControl(
        { value: especialidadRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      descripcion: new FormControl(especialidadRawValue.descripcion, {
        validators: [Validators.required],
      }),
      createdBy: new FormControl(especialidadRawValue.createdBy),
      createdDate: new FormControl(especialidadRawValue.createdDate),
      lastModifiedBy: new FormControl(especialidadRawValue.lastModifiedBy),
      lastModifiedDate: new FormControl(especialidadRawValue.lastModifiedDate),
    });
  }

  getEspecialidad(form: EspecialidadFormGroup): IEspecialidad | NewEspecialidad {
    return this.convertEspecialidadRawValueToEspecialidad(form.getRawValue() as EspecialidadFormRawValue | NewEspecialidadFormRawValue);
  }

  resetForm(form: EspecialidadFormGroup, especialidad: EspecialidadFormGroupInput): void {
    const especialidadRawValue = this.convertEspecialidadToEspecialidadRawValue({ ...this.getFormDefaults(), ...especialidad });
    form.reset(
      {
        ...especialidadRawValue,
        id: { value: especialidadRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): EspecialidadFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      createdDate: currentTime,
      lastModifiedDate: currentTime,
    };
  }

  private convertEspecialidadRawValueToEspecialidad(
    rawEspecialidad: EspecialidadFormRawValue | NewEspecialidadFormRawValue
  ): IEspecialidad | NewEspecialidad {
    return {
      ...rawEspecialidad,
      createdDate: dayjs(rawEspecialidad.createdDate, DATE_TIME_FORMAT),
      lastModifiedDate: dayjs(rawEspecialidad.lastModifiedDate, DATE_TIME_FORMAT),
    };
  }

  private convertEspecialidadToEspecialidadRawValue(
    especialidad: IEspecialidad | (Partial<NewEspecialidad> & EspecialidadFormDefaults)
  ): EspecialidadFormRawValue | PartialWithRequiredKeyOf<NewEspecialidadFormRawValue> {
    return {
      ...especialidad,
      createdDate: especialidad.createdDate ? especialidad.createdDate.format(DATE_TIME_FORMAT) : undefined,
      lastModifiedDate: especialidad.lastModifiedDate ? especialidad.lastModifiedDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
