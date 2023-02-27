import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICiudad } from '../ciudad.model';
import { CiudadService } from '../service/ciudad.service';

@Injectable({ providedIn: 'root' })
export class CiudadRoutingResolveService implements Resolve<ICiudad | null> {
  constructor(protected service: CiudadService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICiudad | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((ciudad: HttpResponse<ICiudad>) => {
          if (ciudad.body) {
            return of(ciudad.body);
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
