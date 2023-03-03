import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IDoctor, NewDoctor } from '../doctor.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IDoctor for edit and NewDoctorFormGroupInput for create.
 */
type DoctorFormGroupInput = IDoctor | PartialWithRequiredKeyOf<NewDoctor>;

type DoctorFormDefaults = Pick<NewDoctor, 'id'>;

type DoctorFormGroupContent = {
  id: FormControl<IDoctor['id'] | NewDoctor['id']>;
  nombre: FormControl<IDoctor['nombre']>;
  correoPersonal: FormControl<IDoctor['correoPersonal']>;
  correoCorporativo: FormControl<IDoctor['correoCorporativo']>;
  telefonoPersonal: FormControl<IDoctor['telefonoPersonal']>;
  telefonoCorporativo: FormControl<IDoctor['telefonoCorporativo']>;
  especialidad: FormControl<IDoctor['especialidad']>;
  user: FormControl<IDoctor['user']>;
};

export type DoctorFormGroup = FormGroup<DoctorFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class DoctorFormService {
  createDoctorFormGroup(doctor: DoctorFormGroupInput = { id: null }): DoctorFormGroup {
    const doctorRawValue = {
      ...this.getFormDefaults(),
      ...doctor,
    };
    return new FormGroup<DoctorFormGroupContent>({
      id: new FormControl(
        { value: doctorRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      nombre: new FormControl(doctorRawValue.nombre, {
        validators: [Validators.required],
      }),
      correoPersonal: new FormControl(doctorRawValue.correoPersonal, {
        validators: [Validators.pattern('^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$')],
      }),
      correoCorporativo: new FormControl(doctorRawValue.correoCorporativo, {
        validators: [Validators.required, Validators.pattern('^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$')],
      }),
      telefonoPersonal: new FormControl(doctorRawValue.telefonoPersonal),
      telefonoCorporativo: new FormControl(doctorRawValue.telefonoCorporativo, {
        validators: [Validators.required],
      }),
      especialidad: new FormControl(doctorRawValue.especialidad),
      user: new FormControl(doctorRawValue.user),
    });
  }

  getDoctor(form: DoctorFormGroup): IDoctor | NewDoctor {
    return form.getRawValue() as IDoctor | NewDoctor;
  }

  resetForm(form: DoctorFormGroup, doctor: DoctorFormGroupInput): void {
    const doctorRawValue = { ...this.getFormDefaults(), ...doctor };
    form.reset(
      {
        ...doctorRawValue,
        id: { value: doctorRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): DoctorFormDefaults {
    return {
      id: null,
    };
  }
}
