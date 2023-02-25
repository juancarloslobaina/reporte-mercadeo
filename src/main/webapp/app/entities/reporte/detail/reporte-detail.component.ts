import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IReporte } from '../reporte.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-reporte-detail',
  templateUrl: './reporte-detail.component.html',
})
export class ReporteDetailComponent implements OnInit {
  reporte: IReporte | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ reporte }) => {
      this.reporte = reporte;
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  previousState(): void {
    window.history.back();
  }
}
