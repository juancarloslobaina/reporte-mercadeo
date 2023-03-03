import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ICiudad, NewCiudad } from '../ciudad.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICiudad for edit and NewCiudadFormGroupInput for create.
 */
type CiudadFormGroupInput = ICiudad | PartialWithRequiredKeyOf<NewCiudad>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ICiudad | NewCiudad> = Omit<T, 'createdDate' | 'lastModifiedDate'> & {
  createdDate?: string | null;
  lastModifiedDate?: string | null;
};

type CiudadFormRawValue = FormValueOf<ICiudad>;

type NewCiudadFormRawValue = FormValueOf<NewCiudad>;

type CiudadFormDefaults = Pick<NewCiudad, 'id' | 'createdDate' | 'lastModifiedDate'>;

type CiudadFormGroupContent = {
  id: FormControl<CiudadFormRawValue['id'] | NewCiudad['id']>;
  nombre: FormControl<CiudadFormRawValue['nombre']>;
  createdBy: FormControl<CiudadFormRawValue['createdBy']>;
  createdDate: FormControl<CiudadFormRawValue['createdDate']>;
  lastModifiedBy: FormControl<CiudadFormRawValue['lastModifiedBy']>;
  lastModifiedDate: FormControl<CiudadFormRawValue['lastModifiedDate']>;
};

export type CiudadFormGroup = FormGroup<CiudadFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CiudadFormService {
  createCiudadFormGroup(ciudad: CiudadFormGroupInput = { id: null }): CiudadFormGroup {
    const ciudadRawValue = this.convertCiudadToCiudadRawValue({
      ...this.getFormDefaults(),
      ...ciudad,
    });
    return new FormGroup<CiudadFormGroupContent>({
      id: new FormControl(
        { value: ciudadRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      nombre: new FormControl(ciudadRawValue.nombre, {
        validators: [Validators.required],
      }),
      createdBy: new FormControl(ciudadRawValue.createdBy),
      createdDate: new FormControl(ciudadRawValue.createdDate),
      lastModifiedBy: new FormControl(ciudadRawValue.lastModifiedBy),
      lastModifiedDate: new FormControl(ciudadRawValue.lastModifiedDate),
    });
  }

  getCiudad(form: CiudadFormGroup): ICiudad | NewCiudad {
    return this.convertCiudadRawValueToCiudad(form.getRawValue() as CiudadFormRawValue | NewCiudadFormRawValue);
  }

  resetForm(form: CiudadFormGroup, ciudad: CiudadFormGroupInput): void {
    const ciudadRawValue = this.convertCiudadToCiudadRawValue({ ...this.getFormDefaults(), ...ciudad });
    form.reset(
      {
        ...ciudadRawValue,
        id: { value: ciudadRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): CiudadFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      createdDate: currentTime,
      lastModifiedDate: currentTime,
    };
  }

  private convertCiudadRawValueToCiudad(rawCiudad: CiudadFormRawValue | NewCiudadFormRawValue): ICiudad | NewCiudad {
    return {
      ...rawCiudad,
      createdDate: dayjs(rawCiudad.createdDate, DATE_TIME_FORMAT),
      lastModifiedDate: dayjs(rawCiudad.lastModifiedDate, DATE_TIME_FORMAT),
    };
  }

  private convertCiudadToCiudadRawValue(
    ciudad: ICiudad | (Partial<NewCiudad> & CiudadFormDefaults)
  ): CiudadFormRawValue | PartialWithRequiredKeyOf<NewCiudadFormRawValue> {
    return {
      ...ciudad,
      createdDate: ciudad.createdDate ? ciudad.createdDate.format(DATE_TIME_FORMAT) : undefined,
      lastModifiedDate: ciudad.lastModifiedDate ? ciudad.lastModifiedDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
