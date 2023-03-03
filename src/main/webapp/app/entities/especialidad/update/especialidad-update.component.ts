import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { EspecialidadFormService, EspecialidadFormGroup } from './especialidad-form.service';
import { IEspecialidad } from '../especialidad.model';
import { EspecialidadService } from '../service/especialidad.service';

@Component({
  selector: 'jhi-especialidad-update',
  templateUrl: './especialidad-update.component.html',
})
export class EspecialidadUpdateComponent implements OnInit {
  isSaving = false;
  especialidad: IEspecialidad | null = null;

  editForm: EspecialidadFormGroup = this.especialidadFormService.createEspecialidadFormGroup();

  constructor(
    protected especialidadService: EspecialidadService,
    protected especialidadFormService: EspecialidadFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ especialidad }) => {
      this.especialidad = especialidad;
      if (especialidad) {
        this.updateForm(especialidad);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const especialidad = this.especialidadFormService.getEspecialidad(this.editForm);
    if (especialidad.id !== null) {
      this.subscribeToSaveResponse(this.especialidadService.update(especialidad));
    } else {
      this.subscribeToSaveResponse(this.especialidadService.create(especialidad));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEspecialidad>>): void {
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

  protected updateForm(especialidad: IEspecialidad): void {
    this.especialidad = especialidad;
    this.especialidadFormService.resetForm(this.editForm, especialidad);
  }
}
