import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICentroMedico } from '../centro-medico.model';
import { CentroMedicoService } from '../service/centro-medico.service';

@Injectable({ providedIn: 'root' })
export class CentroMedicoRoutingResolveService implements Resolve<ICentroMedico | null> {
  constructor(protected service: CentroMedicoService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICentroMedico | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((centroMedico: HttpResponse<ICentroMedico>) => {
          if (centroMedico.body) {
            return of(centroMedico.body);
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
