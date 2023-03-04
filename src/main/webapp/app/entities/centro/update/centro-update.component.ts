import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { CentroFormService, CentroFormGroup } from './centro-form.service';
import { ICentro } from '../centro.model';
import { CentroService } from '../service/centro.service';
import { ICiudad } from 'app/entities/ciudad/ciudad.model';
import { CiudadService } from 'app/entities/ciudad/service/ciudad.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

@Component({
  selector: 'jhi-centro-update',
  templateUrl: './centro-update.component.html',
})
export class CentroUpdateComponent implements OnInit {
  isSaving = false;
  centro: ICentro | null = null;

  ciudadsSharedCollection: ICiudad[] = [];
  usersSharedCollection: IUser[] = [];

  editForm: CentroFormGroup = this.centroFormService.createCentroFormGroup();

  constructor(
    protected centroService: CentroService,
    protected centroFormService: CentroFormService,
    protected ciudadService: CiudadService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareCiudad = (o1: ICiudad | null, o2: ICiudad | null): boolean => this.ciudadService.compareCiudad(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ centro }) => {
      this.centro = centro;
      if (centro) {
        this.updateForm(centro);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const centro = this.centroFormService.getCentro(this.editForm);
    if (centro.id !== null) {
      this.subscribeToSaveResponse(this.centroService.update(centro));
    } else {
      this.subscribeToSaveResponse(this.centroService.create(centro));
    }
  }

  buscarCiudad($event: any): void {
    const queryObject: any = {
      page: 0,
      size: 20,
      'nombre.contains': $event.query,
      sort: ['id,asc'],
    };
    this.ciudadService
      .search(queryObject)
      .pipe(map((res: HttpResponse<ICiudad[]>) => res.body ?? []))
      .pipe(map((ciudads: ICiudad[]) => this.ciudadService.addCiudadToCollectionIfMissing<ICiudad>(ciudads, this.centro?.ciudad)))
      .subscribe((ciudads: ICiudad[]) => (this.ciudadsSharedCollection = ciudads));
  }

  buscarUsuario($event: any): void {
    const queryObject: any = {
      page: 0,
      size: 20,
      query: $event.query,
      sort: ['id,asc'],
    };
    this.userService
      .search(queryObject)
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.centro?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICentro>>): void {
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

  protected updateForm(centro: ICentro): void {
    this.centro = centro;
    this.centroFormService.resetForm(this.editForm, centro);

    this.ciudadsSharedCollection = this.ciudadService.addCiudadToCollectionIfMissing<ICiudad>(this.ciudadsSharedCollection, centro.ciudad);
    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, centro.user);
  }

  protected loadRelationshipsOptions(): void {
    this.ciudadService
      .query()
      .pipe(map((res: HttpResponse<ICiudad[]>) => res.body ?? []))
      .pipe(map((ciudads: ICiudad[]) => this.ciudadService.addCiudadToCollectionIfMissing<ICiudad>(ciudads, this.centro?.ciudad)))
      .subscribe((ciudads: ICiudad[]) => (this.ciudadsSharedCollection = ciudads));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.centro?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
