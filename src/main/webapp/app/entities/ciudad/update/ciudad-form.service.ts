import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

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

type CiudadFormDefaults = Pick<NewCiudad, 'id'>;

type CiudadFormGroupContent = {
  id: FormControl<ICiudad['id'] | NewCiudad['id']>;
  nombre: FormControl<ICiudad['nombre']>;
};

export type CiudadFormGroup = FormGroup<CiudadFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CiudadFormService {
  createCiudadFormGroup(ciudad: CiudadFormGroupInput = { id: null }): CiudadFormGroup {
    const ciudadRawValue = {
      ...this.getFormDefaults(),
      ...ciudad,
    };
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
    });
  }

  getCiudad(form: CiudadFormGroup): ICiudad | NewCiudad {
    return form.getRawValue() as ICiudad | NewCiudad;
  }

  resetForm(form: CiudadFormGroup, ciudad: CiudadFormGroupInput): void {
    const ciudadRawValue = { ...this.getFormDefaults(), ...ciudad };
    form.reset(
      {
        ...ciudadRawValue,
        id: { value: ciudadRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): CiudadFormDefaults {
    return {
      id: null,
    };
  }
}
