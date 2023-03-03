import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IEspecialidad } from '../especialidad.model';
import { EspecialidadService } from '../service/especialidad.service';

@Injectable({ providedIn: 'root' })
export class EspecialidadRoutingResolveService implements Resolve<IEspecialidad | null> {
  constructor(protected service: EspecialidadService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IEspecialidad | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((especialidad: HttpResponse<IEspecialidad>) => {
          if (especialidad.body) {
            return of(especialidad.body);
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
