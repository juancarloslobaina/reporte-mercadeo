import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICiudad, NewCiudad } from '../ciudad.model';
import { SearchWithPagination } from '../../../core/request/request.model';

export type PartialUpdateCiudad = Partial<ICiudad> & Pick<ICiudad, 'id'>;

type RestOf<T extends ICiudad | NewCiudad> = Omit<T, 'createdDate' | 'lastModifiedDate'> & {
  createdDate?: string | null;
  lastModifiedDate?: string | null;
};

export type RestCiudad = RestOf<ICiudad>;

export type NewRestCiudad = RestOf<NewCiudad>;

export type PartialUpdateRestCiudad = RestOf<PartialUpdateCiudad>;

export type EntityResponseType = HttpResponse<ICiudad>;
export type EntityArrayResponseType = HttpResponse<ICiudad[]>;

@Injectable({ providedIn: 'root' })
export class CiudadService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/ciudads');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(ciudad: NewCiudad): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(ciudad);
    return this.http
      .post<RestCiudad>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(ciudad: ICiudad): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(ciudad);
    return this.http
      .put<RestCiudad>(`${this.resourceUrl}/${this.getCiudadIdentifier(ciudad)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(ciudad: PartialUpdateCiudad): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(ciudad);
    return this.http
      .patch<RestCiudad>(`${this.resourceUrl}/${this.getCiudadIdentifier(ciudad)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestCiudad>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestCiudad[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestCiudad[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getCiudadIdentifier(ciudad: Pick<ICiudad, 'id'>): number {
    return ciudad.id;
  }

  compareCiudad(o1: Pick<ICiudad, 'id'> | null, o2: Pick<ICiudad, 'id'> | null): boolean {
    return o1 && o2 ? this.getCiudadIdentifier(o1) === this.getCiudadIdentifier(o2) : o1 === o2;
  }

  addCiudadToCollectionIfMissing<Type extends Pick<ICiudad, 'id'>>(
    ciudadCollection: Type[],
    ...ciudadsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const ciudads: Type[] = ciudadsToCheck.filter(isPresent);
    if (ciudads.length > 0) {
      const ciudadCollectionIdentifiers = ciudadCollection.map(ciudadItem => this.getCiudadIdentifier(ciudadItem)!);
      const ciudadsToAdd = ciudads.filter(ciudadItem => {
        const ciudadIdentifier = this.getCiudadIdentifier(ciudadItem);
        if (ciudadCollectionIdentifiers.includes(ciudadIdentifier)) {
          return false;
        }
        ciudadCollectionIdentifiers.push(ciudadIdentifier);
        return true;
      });
      return [...ciudadsToAdd, ...ciudadCollection];
    }
    return ciudadCollection;
  }

  protected convertDateFromClient<T extends ICiudad | NewCiudad | PartialUpdateCiudad>(ciudad: T): RestOf<T> {
    return {
      ...ciudad,
      createdDate: ciudad.createdDate?.toJSON() ?? null,
      lastModifiedDate: ciudad.lastModifiedDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restCiudad: RestCiudad): ICiudad {
    return {
      ...restCiudad,
      createdDate: restCiudad.createdDate ? dayjs(restCiudad.createdDate) : undefined,
      lastModifiedDate: restCiudad.lastModifiedDate ? dayjs(restCiudad.lastModifiedDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestCiudad>): HttpResponse<ICiudad> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestCiudad[]>): HttpResponse<ICiudad[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
