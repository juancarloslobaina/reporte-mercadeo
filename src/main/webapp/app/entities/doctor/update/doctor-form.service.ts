import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
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

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IDoctor | NewDoctor> = Omit<T, 'createdDate' | 'lastModifiedDate'> & {
  createdDate?: string | null;
  lastModifiedDate?: string | null;
};

type DoctorFormRawValue = FormValueOf<IDoctor>;

type NewDoctorFormRawValue = FormValueOf<NewDoctor>;

type DoctorFormDefaults = Pick<NewDoctor, 'id' | 'createdDate' | 'lastModifiedDate'>;

type DoctorFormGroupContent = {
  id: FormControl<DoctorFormRawValue['id'] | NewDoctor['id']>;
  nombre: FormControl<DoctorFormRawValue['nombre']>;
  correoPersonal: FormControl<DoctorFormRawValue['correoPersonal']>;
  correoCorporativo: FormControl<DoctorFormRawValue['correoCorporativo']>;
  telefonoPersonal: FormControl<DoctorFormRawValue['telefonoPersonal']>;
  telefonoCorporativo: FormControl<DoctorFormRawValue['telefonoCorporativo']>;
  createdBy: FormControl<DoctorFormRawValue['createdBy']>;
  createdDate: FormControl<DoctorFormRawValue['createdDate']>;
  lastModifiedBy: FormControl<DoctorFormRawValue['lastModifiedBy']>;
  lastModifiedDate: FormControl<DoctorFormRawValue['lastModifiedDate']>;
  especialidad: FormControl<DoctorFormRawValue['especialidad']>;
  user: FormControl<DoctorFormRawValue['user']>;
};

export type DoctorFormGroup = FormGroup<DoctorFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class DoctorFormService {
  createDoctorFormGroup(doctor: DoctorFormGroupInput = { id: null }): DoctorFormGroup {
    const doctorRawValue = this.convertDoctorToDoctorRawValue({
      ...this.getFormDefaults(),
      ...doctor,
    });
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
      createdBy: new FormControl(doctorRawValue.createdBy),
      createdDate: new FormControl(doctorRawValue.createdDate),
      lastModifiedBy: new FormControl(doctorRawValue.lastModifiedBy),
      lastModifiedDate: new FormControl(doctorRawValue.lastModifiedDate),
      especialidad: new FormControl(doctorRawValue.especialidad),
      user: new FormControl(doctorRawValue.user),
    });
  }

  getDoctor(form: DoctorFormGroup): IDoctor | NewDoctor {
    return this.convertDoctorRawValueToDoctor(form.getRawValue() as DoctorFormRawValue | NewDoctorFormRawValue);
  }

  resetForm(form: DoctorFormGroup, doctor: DoctorFormGroupInput): void {
    const doctorRawValue = this.convertDoctorToDoctorRawValue({ ...this.getFormDefaults(), ...doctor });
    form.reset(
      {
        ...doctorRawValue,
        id: { value: doctorRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): DoctorFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      createdDate: currentTime,
      lastModifiedDate: currentTime,
    };
  }

  private convertDoctorRawValueToDoctor(rawDoctor: DoctorFormRawValue | NewDoctorFormRawValue): IDoctor | NewDoctor {
    return {
      ...rawDoctor,
      createdDate: dayjs(rawDoctor.createdDate, DATE_TIME_FORMAT),
      lastModifiedDate: dayjs(rawDoctor.lastModifiedDate, DATE_TIME_FORMAT),
    };
  }

  private convertDoctorToDoctorRawValue(
    doctor: IDoctor | (Partial<NewDoctor> & DoctorFormDefaults)
  ): DoctorFormRawValue | PartialWithRequiredKeyOf<NewDoctorFormRawValue> {
    return {
      ...doctor,
      createdDate: doctor.createdDate ? doctor.createdDate.format(DATE_TIME_FORMAT) : undefined,
      lastModifiedDate: doctor.lastModifiedDate ? doctor.lastModifiedDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
