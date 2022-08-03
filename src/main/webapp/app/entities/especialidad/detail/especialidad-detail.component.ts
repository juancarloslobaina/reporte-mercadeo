import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IEspecialidad } from '../especialidad.model';

@Component({
  selector: 'jhi-especialidad-detail',
  templateUrl: './especialidad-detail.component.html',
})
export class EspecialidadDetailComponent implements OnInit {
  especialidad: IEspecialidad | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ especialidad }) => {
      this.especialidad = especialidad;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
