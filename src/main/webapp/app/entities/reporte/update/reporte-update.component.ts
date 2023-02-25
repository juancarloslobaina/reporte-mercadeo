import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ReporteFormService, ReporteFormGroup } from './reporte-form.service';
import { IReporte } from '../reporte.model';
import { ReporteService } from '../service/reporte.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { ICentroMedico } from 'app/entities/centro-medico/centro-medico.model';
import { CentroMedicoService } from 'app/entities/centro-medico/service/centro-medico.service';
import { IDoctor } from 'app/entities/doctor/doctor.model';
import { DoctorService } from 'app/entities/doctor/service/doctor.service';

@Component({
  selector: 'jhi-reporte-update',
  templateUrl: './reporte-update.component.html',
})
export class ReporteUpdateComponent implements OnInit {
  isSaving = false;
  reporte: IReporte | null = null;

  centroMedicosSharedCollection: ICentroMedico[] = [];
  doctorsSharedCollection: IDoctor[] = [];

  editForm: ReporteFormGroup = this.reporteFormService.createReporteFormGroup();

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected reporteService: ReporteService,
    protected reporteFormService: ReporteFormService,
    protected centroMedicoService: CentroMedicoService,
    protected doctorService: DoctorService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareCentroMedico = (o1: ICentroMedico | null, o2: ICentroMedico | null): boolean =>
    this.centroMedicoService.compareCentroMedico(o1, o2);

  compareDoctor = (o1: IDoctor | null, o2: IDoctor | null): boolean => this.doctorService.compareDoctor(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ reporte }) => {
      this.reporte = reporte;
      if (reporte) {
        this.updateForm(reporte);
      }

      this.loadRelationshipsOptions();
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('reportemercadeoApp.error', { message: err.message })),
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const reporte = this.reporteFormService.getReporte(this.editForm);
    if (reporte.id !== null) {
      this.subscribeToSaveResponse(this.reporteService.update(reporte));
    } else {
      this.subscribeToSaveResponse(this.reporteService.create(reporte));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IReporte>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(reporte: IReporte): void {
    this.reporte = reporte;
    this.reporteFormService.resetForm(this.editForm, reporte);

    this.centroMedicosSharedCollection = this.centroMedicoService.addCentroMedicoToCollectionIfMissing<ICentroMedico>(
      this.centroMedicosSharedCollection,
      reporte.centroMedico
    );
    this.doctorsSharedCollection = this.doctorService.addDoctorToCollectionIfMissing<IDoctor>(this.doctorsSharedCollection, reporte.doctor);
  }

  protected loadRelationshipsOptions(): void {
    this.centroMedicoService
      .query()
      .pipe(map((res: HttpResponse<ICentroMedico[]>) => res.body ?? []))
      .pipe(
        map((centroMedicos: ICentroMedico[]) =>
          this.centroMedicoService.addCentroMedicoToCollectionIfMissing<ICentroMedico>(centroMedicos, this.reporte?.centroMedico)
        )
      )
      .subscribe((centroMedicos: ICentroMedico[]) => (this.centroMedicosSharedCollection = centroMedicos));

    this.doctorService
      .query()
      .pipe(map((res: HttpResponse<IDoctor[]>) => res.body ?? []))
      .pipe(map((doctors: IDoctor[]) => this.doctorService.addDoctorToCollectionIfMissing<IDoctor>(doctors, this.reporte?.doctor)))
      .subscribe((doctors: IDoctor[]) => (this.doctorsSharedCollection = doctors));
  }
}
