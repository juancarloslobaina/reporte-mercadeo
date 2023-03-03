import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ICentro, NewCentro } from '../centro.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICentro for edit and NewCentroFormGroupInput for create.
 */
type CentroFormGroupInput = ICentro | PartialWithRequiredKeyOf<NewCentro>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ICentro | NewCentro> = Omit<T, 'createdDate' | 'lastModifiedDate'> & {
  createdDate?: string | null;
  lastModifiedDate?: string | null;
};

type CentroFormRawValue = FormValueOf<ICentro>;

type NewCentroFormRawValue = FormValueOf<NewCentro>;

type CentroFormDefaults = Pick<NewCentro, 'id' | 'createdDate' | 'lastModifiedDate'>;

type CentroFormGroupContent = {
  id: FormControl<CentroFormRawValue['id'] | NewCentro['id']>;
  nombre: FormControl<CentroFormRawValue['nombre']>;
  createdBy: FormControl<CentroFormRawValue['createdBy']>;
  createdDate: FormControl<CentroFormRawValue['createdDate']>;
  lastModifiedBy: FormControl<CentroFormRawValue['lastModifiedBy']>;
  lastModifiedDate: FormControl<CentroFormRawValue['lastModifiedDate']>;
  ciudad: FormControl<CentroFormRawValue['ciudad']>;
  user: FormControl<CentroFormRawValue['user']>;
};

export type CentroFormGroup = FormGroup<CentroFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CentroFormService {
  createCentroFormGroup(centro: CentroFormGroupInput = { id: null }): CentroFormGroup {
    const centroRawValue = this.convertCentroToCentroRawValue({
      ...this.getFormDefaults(),
      ...centro,
    });
    return new FormGroup<CentroFormGroupContent>({
      id: new FormControl(
        { value: centroRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      nombre: new FormControl(centroRawValue.nombre, {
        validators: [Validators.required],
      }),
      createdBy: new FormControl(centroRawValue.createdBy),
      createdDate: new FormControl(centroRawValue.createdDate),
      lastModifiedBy: new FormControl(centroRawValue.lastModifiedBy),
      lastModifiedDate: new FormControl(centroRawValue.lastModifiedDate),
      ciudad: new FormControl(centroRawValue.ciudad),
      user: new FormControl(centroRawValue.user),
    });
  }

  getCentro(form: CentroFormGroup): ICentro | NewCentro {
    return this.convertCentroRawValueToCentro(form.getRawValue() as CentroFormRawValue | NewCentroFormRawValue);
  }

  resetForm(form: CentroFormGroup, centro: CentroFormGroupInput): void {
    const centroRawValue = this.convertCentroToCentroRawValue({ ...this.getFormDefaults(), ...centro });
    form.reset(
      {
        ...centroRawValue,
        id: { value: centroRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): CentroFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      createdDate: currentTime,
      lastModifiedDate: currentTime,
    };
  }

  private convertCentroRawValueToCentro(rawCentro: CentroFormRawValue | NewCentroFormRawValue): ICentro | NewCentro {
    return {
      ...rawCentro,
      createdDate: dayjs(rawCentro.createdDate, DATE_TIME_FORMAT),
      lastModifiedDate: dayjs(rawCentro.lastModifiedDate, DATE_TIME_FORMAT),
    };
  }

  private convertCentroToCentroRawValue(
    centro: ICentro | (Partial<NewCentro> & CentroFormDefaults)
  ): CentroFormRawValue | PartialWithRequiredKeyOf<NewCentroFormRawValue> {
    return {
      ...centro,
      createdDate: centro.createdDate ? centro.createdDate.format(DATE_TIME_FORMAT) : undefined,
      lastModifiedDate: centro.lastModifiedDate ? centro.lastModifiedDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
