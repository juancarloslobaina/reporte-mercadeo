import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { CiudadFormService, CiudadFormGroup } from './ciudad-form.service';
import { ICiudad } from '../ciudad.model';
import { CiudadService } from '../service/ciudad.service';

@Component({
  selector: 'jhi-ciudad-update',
  templateUrl: './ciudad-update.component.html',
})
export class CiudadUpdateComponent implements OnInit {
  isSaving = false;
  ciudad: ICiudad | null = null;

  editForm: CiudadFormGroup = this.ciudadFormService.createCiudadFormGroup();

  constructor(
    protected ciudadService: CiudadService,
    protected ciudadFormService: CiudadFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ ciudad }) => {
      this.ciudad = ciudad;
      if (ciudad) {
        this.updateForm(ciudad);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const ciudad = this.ciudadFormService.getCiudad(this.editForm);
    if (ciudad.id !== null) {
      this.subscribeToSaveResponse(this.ciudadService.update(ciudad));
    } else {
      this.subscribeToSaveResponse(this.ciudadService.create(ciudad));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICiudad>>): void {
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

  protected updateForm(ciudad: ICiudad): void {
    this.ciudad = ciudad;
    this.ciudadFormService.resetForm(this.editForm, ciudad);
  }
}
