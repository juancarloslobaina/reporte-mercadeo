import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICentro } from '../centro.model';

@Component({
  selector: 'jhi-centro-detail',
  templateUrl: './centro-detail.component.html',
})
export class CentroDetailComponent implements OnInit {
  centro: ICentro | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ centro }) => {
      this.centro = centro;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
