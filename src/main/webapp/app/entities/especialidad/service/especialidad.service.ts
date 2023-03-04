import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IEspecialidad, NewEspecialidad } from '../especialidad.model';
import { SearchWithPagination } from '../../../core/request/request.model';

export type PartialUpdateEspecialidad = Partial<IEspecialidad> & Pick<IEspecialidad, 'id'>;

type RestOf<T extends IEspecialidad | NewEspecialidad> = Omit<T, 'createdDate' | 'lastModifiedDate'> & {
  createdDate?: string | null;
  lastModifiedDate?: string | null;
};

export type RestEspecialidad = RestOf<IEspecialidad>;

export type NewRestEspecialidad = RestOf<NewEspecialidad>;

export type PartialUpdateRestEspecialidad = RestOf<PartialUpdateEspecialidad>;

export type EntityResponseType = HttpResponse<IEspecialidad>;
export type EntityArrayResponseType = HttpResponse<IEspecialidad[]>;

@Injectable({ providedIn: 'root' })
export class EspecialidadService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/especialidads');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(especialidad: NewEspecialidad): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(especialidad);
    return this.http
      .post<RestEspecialidad>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(especialidad: IEspecialidad): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(especialidad);
    return this.http
      .put<RestEspecialidad>(`${this.resourceUrl}/${this.getEspecialidadIdentifier(especialidad)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(especialidad: PartialUpdateEspecialidad): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(especialidad);
    return this.http
      .patch<RestEspecialidad>(`${this.resourceUrl}/${this.getEspecialidadIdentifier(especialidad)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestEspecialidad>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestEspecialidad[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestEspecialidad[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getEspecialidadIdentifier(especialidad: Pick<IEspecialidad, 'id'>): number {
    return especialidad.id;
  }

  compareEspecialidad(o1: Pick<IEspecialidad, 'id'> | null, o2: Pick<IEspecialidad, 'id'> | null): boolean {
    return o1 && o2 ? this.getEspecialidadIdentifier(o1) === this.getEspecialidadIdentifier(o2) : o1 === o2;
  }

  addEspecialidadToCollectionIfMissing<Type extends Pick<IEspecialidad, 'id'>>(
    especialidadCollection: Type[],
    ...especialidadsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const especialidads: Type[] = especialidadsToCheck.filter(isPresent);
    if (especialidads.length > 0) {
      const especialidadCollectionIdentifiers = especialidadCollection.map(
        especialidadItem => this.getEspecialidadIdentifier(especialidadItem)!
      );
      const especialidadsToAdd = especialidads.filter(especialidadItem => {
        const especialidadIdentifier = this.getEspecialidadIdentifier(especialidadItem);
        if (especialidadCollectionIdentifiers.includes(especialidadIdentifier)) {
          return false;
        }
        especialidadCollectionIdentifiers.push(especialidadIdentifier);
        return true;
      });
      return [...especialidadsToAdd, ...especialidadCollection];
    }
    return especialidadCollection;
  }

  protected convertDateFromClient<T extends IEspecialidad | NewEspecialidad | PartialUpdateEspecialidad>(especialidad: T): RestOf<T> {
    return {
      ...especialidad,
      createdDate: especialidad.createdDate?.toJSON() ?? null,
      lastModifiedDate: especialidad.lastModifiedDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restEspecialidad: RestEspecialidad): IEspecialidad {
    return {
      ...restEspecialidad,
      createdDate: restEspecialidad.createdDate ? dayjs(restEspecialidad.createdDate) : undefined,
      lastModifiedDate: restEspecialidad.lastModifiedDate ? dayjs(restEspecialidad.lastModifiedDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestEspecialidad>): HttpResponse<IEspecialidad> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestEspecialidad[]>): HttpResponse<IEspecialidad[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
