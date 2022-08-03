import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICentroMedico } from '../centro-medico.model';

@Component({
  selector: 'jhi-centro-medico-detail',
  templateUrl: './centro-medico-detail.component.html',
})
export class CentroMedicoDetailComponent implements OnInit {
  centroMedico: ICentroMedico | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ centroMedico }) => {
      this.centroMedico = centroMedico;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
