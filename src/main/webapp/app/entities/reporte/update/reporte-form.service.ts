import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IReporte, NewReporte } from '../reporte.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IReporte for edit and NewReporteFormGroupInput for create.
 */
type ReporteFormGroupInput = IReporte | PartialWithRequiredKeyOf<NewReporte>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IReporte | NewReporte> = Omit<T, 'fecha' | 'createdDate' | 'lastModifiedDate'> & {
  fecha?: string | null;
  createdDate?: string | null;
  lastModifiedDate?: string | null;
};

type ReporteFormRawValue = FormValueOf<IReporte>;

type NewReporteFormRawValue = FormValueOf<NewReporte>;

type ReporteFormDefaults = Pick<NewReporte, 'id' | 'fecha' | 'createdDate' | 'lastModifiedDate'>;

type ReporteFormGroupContent = {
  id: FormControl<ReporteFormRawValue['id'] | NewReporte['id']>;
  descripcion: FormControl<ReporteFormRawValue['descripcion']>;
  fecha: FormControl<ReporteFormRawValue['fecha']>;
  createdBy: FormControl<ReporteFormRawValue['createdBy']>;
  createdDate: FormControl<ReporteFormRawValue['createdDate']>;
  lastModifiedBy: FormControl<ReporteFormRawValue['lastModifiedBy']>;
  lastModifiedDate: FormControl<ReporteFormRawValue['lastModifiedDate']>;
  centro: FormControl<ReporteFormRawValue['centro']>;
  doctor: FormControl<ReporteFormRawValue['doctor']>;
  user: FormControl<ReporteFormRawValue['user']>;
};

export type ReporteFormGroup = FormGroup<ReporteFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ReporteFormService {
  createReporteFormGroup(reporte: ReporteFormGroupInput = { id: null }): ReporteFormGroup {
    const reporteRawValue = this.convertReporteToReporteRawValue({
      ...this.getFormDefaults(),
      ...reporte,
    });
    return new FormGroup<ReporteFormGroupContent>({
      id: new FormControl(
        { value: reporteRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      descripcion: new FormControl(reporteRawValue.descripcion, {
        validators: [Validators.required],
      }),
      fecha: new FormControl(reporteRawValue.fecha, {
        validators: [Validators.required],
      }),
      createdBy: new FormControl(reporteRawValue.createdBy),
      createdDate: new FormControl(reporteRawValue.createdDate),
      lastModifiedBy: new FormControl(reporteRawValue.lastModifiedBy),
      lastModifiedDate: new FormControl(reporteRawValue.lastModifiedDate),
      centro: new FormControl(reporteRawValue.centro),
      doctor: new FormControl(reporteRawValue.doctor),
      user: new FormControl(reporteRawValue.user),
    });
  }

  getReporte(form: ReporteFormGroup): IReporte | NewReporte {
    return this.convertReporteRawValueToReporte(form.getRawValue() as ReporteFormRawValue | NewReporteFormRawValue);
  }

  resetForm(form: ReporteFormGroup, reporte: ReporteFormGroupInput): void {
    const reporteRawValue = this.convertReporteToReporteRawValue({ ...this.getFormDefaults(), ...reporte });
    form.reset(
      {
        ...reporteRawValue,
        id: { value: reporteRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ReporteFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      fecha: currentTime,
      createdDate: currentTime,
      lastModifiedDate: currentTime,
    };
  }

  private convertReporteRawValueToReporte(rawReporte: ReporteFormRawValue | NewReporteFormRawValue): IReporte | NewReporte {
    return {
      ...rawReporte,
      fecha: dayjs(rawReporte.fecha, DATE_TIME_FORMAT),
      createdDate: dayjs(rawReporte.createdDate, DATE_TIME_FORMAT),
      lastModifiedDate: dayjs(rawReporte.lastModifiedDate, DATE_TIME_FORMAT),
    };
  }

  private convertReporteToReporteRawValue(
    reporte: IReporte | (Partial<NewReporte> & ReporteFormDefaults)
  ): ReporteFormRawValue | PartialWithRequiredKeyOf<NewReporteFormRawValue> {
    return {
      ...reporte,
      fecha: reporte.fecha ? reporte.fecha.format(DATE_TIME_FORMAT) : undefined,
      createdDate: reporte.createdDate ? reporte.createdDate.format(DATE_TIME_FORMAT) : undefined,
      lastModifiedDate: reporte.lastModifiedDate ? reporte.lastModifiedDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
