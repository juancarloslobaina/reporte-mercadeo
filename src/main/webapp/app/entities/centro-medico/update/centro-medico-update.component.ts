import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { CentroMedicoFormService, CentroMedicoFormGroup } from './centro-medico-form.service';
import { ICentroMedico } from '../centro-medico.model';
import { CentroMedicoService } from '../service/centro-medico.service';
import { ICiudad } from 'app/entities/ciudad/ciudad.model';
import { CiudadService } from 'app/entities/ciudad/service/ciudad.service';

@Component({
  selector: 'jhi-centro-medico-update',
  templateUrl: './centro-medico-update.component.html',
})
export class CentroMedicoUpdateComponent implements OnInit {
  isSaving = false;
  centroMedico: ICentroMedico | null = null;

  ciudadsSharedCollection: ICiudad[] = [];

  editForm: CentroMedicoFormGroup = this.centroMedicoFormService.createCentroMedicoFormGroup();

  constructor(
    protected centroMedicoService: CentroMedicoService,
    protected centroMedicoFormService: CentroMedicoFormService,
    protected ciudadService: CiudadService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareCiudad = (o1: ICiudad | null, o2: ICiudad | null): boolean => this.ciudadService.compareCiudad(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ centroMedico }) => {
      this.centroMedico = centroMedico;
      if (centroMedico) {
        this.updateForm(centroMedico);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const centroMedico = this.centroMedicoFormService.getCentroMedico(this.editForm);
    if (centroMedico.id !== null) {
      this.subscribeToSaveResponse(this.centroMedicoService.update(centroMedico));
    } else {
      this.subscribeToSaveResponse(this.centroMedicoService.create(centroMedico));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICentroMedico>>): void {
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

  protected updateForm(centroMedico: ICentroMedico): void {
    this.centroMedico = centroMedico;
    this.centroMedicoFormService.resetForm(this.editForm, centroMedico);

    this.ciudadsSharedCollection = this.ciudadService.addCiudadToCollectionIfMissing<ICiudad>(
      this.ciudadsSharedCollection,
      centroMedico.ciudad
    );
  }

  protected loadRelationshipsOptions(): void {
    this.ciudadService
      .query()
      .pipe(map((res: HttpResponse<ICiudad[]>) => res.body ?? []))
      .pipe(map((ciudads: ICiudad[]) => this.ciudadService.addCiudadToCollectionIfMissing<ICiudad>(ciudads, this.centroMedico?.ciudad)))
      .subscribe((ciudads: ICiudad[]) => (this.ciudadsSharedCollection = ciudads));
  }
}
