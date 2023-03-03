import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { DoctorFormService, DoctorFormGroup } from './doctor-form.service';
import { IDoctor } from '../doctor.model';
import { DoctorService } from '../service/doctor.service';
import { IEspecialidad } from 'app/entities/especialidad/especialidad.model';
import { EspecialidadService } from 'app/entities/especialidad/service/especialidad.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

@Component({
  selector: 'jhi-doctor-update',
  templateUrl: './doctor-update.component.html',
})
export class DoctorUpdateComponent implements OnInit {
  isSaving = false;
  doctor: IDoctor | null = null;

  especialidadsSharedCollection: IEspecialidad[] = [];
  usersSharedCollection: IUser[] = [];

  editForm: DoctorFormGroup = this.doctorFormService.createDoctorFormGroup();

  constructor(
    protected doctorService: DoctorService,
    protected doctorFormService: DoctorFormService,
    protected especialidadService: EspecialidadService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareEspecialidad = (o1: IEspecialidad | null, o2: IEspecialidad | null): boolean =>
    this.especialidadService.compareEspecialidad(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ doctor }) => {
      this.doctor = doctor;
      if (doctor) {
        this.updateForm(doctor);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const doctor = this.doctorFormService.getDoctor(this.editForm);
    if (doctor.id !== null) {
      this.subscribeToSaveResponse(this.doctorService.update(doctor));
    } else {
      this.subscribeToSaveResponse(this.doctorService.create(doctor));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDoctor>>): void {
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

  protected updateForm(doctor: IDoctor): void {
    this.doctor = doctor;
    this.doctorFormService.resetForm(this.editForm, doctor);

    this.especialidadsSharedCollection = this.especialidadService.addEspecialidadToCollectionIfMissing<IEspecialidad>(
      this.especialidadsSharedCollection,
      doctor.especialidad
    );
    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, doctor.user);
  }

  protected loadRelationshipsOptions(): void {
    this.especialidadService
      .query()
      .pipe(map((res: HttpResponse<IEspecialidad[]>) => res.body ?? []))
      .pipe(
        map((especialidads: IEspecialidad[]) =>
          this.especialidadService.addEspecialidadToCollectionIfMissing<IEspecialidad>(especialidads, this.doctor?.especialidad)
        )
      )
      .subscribe((especialidads: IEspecialidad[]) => (this.especialidadsSharedCollection = especialidads));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.doctor?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
