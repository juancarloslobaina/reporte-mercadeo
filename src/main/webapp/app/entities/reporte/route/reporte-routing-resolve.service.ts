import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IReporte } from '../reporte.model';
import { ReporteService } from '../service/reporte.service';

@Injectable({ providedIn: 'root' })
export class ReporteRoutingResolveService implements Resolve<IReporte | null> {
  constructor(protected service: ReporteService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IReporte | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((reporte: HttpResponse<IReporte>) => {
          if (reporte.body) {
            return of(reporte.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
