import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICentro, NewCentro } from '../centro.model';
import { SearchWithPagination } from '../../../core/request/request.model';

export type PartialUpdateCentro = Partial<ICentro> & Pick<ICentro, 'id'>;

type RestOf<T extends ICentro | NewCentro> = Omit<T, 'createdDate' | 'lastModifiedDate'> & {
  createdDate?: string | null;
  lastModifiedDate?: string | null;
};

export type RestCentro = RestOf<ICentro>;

export type NewRestCentro = RestOf<NewCentro>;

export type PartialUpdateRestCentro = RestOf<PartialUpdateCentro>;

export type EntityResponseType = HttpResponse<ICentro>;
export type EntityArrayResponseType = HttpResponse<ICentro[]>;

@Injectable({ providedIn: 'root' })
export class CentroService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/centros');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(centro: NewCentro): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(centro);
    return this.http
      .post<RestCentro>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(centro: ICentro): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(centro);
    return this.http
      .put<RestCentro>(`${this.resourceUrl}/${this.getCentroIdentifier(centro)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(centro: PartialUpdateCentro): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(centro);
    return this.http
      .patch<RestCentro>(`${this.resourceUrl}/${this.getCentroIdentifier(centro)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestCentro>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestCentro[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestCentro[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getCentroIdentifier(centro: Pick<ICentro, 'id'>): number {
    return centro.id;
  }

  compareCentro(o1: Pick<ICentro, 'id'> | null, o2: Pick<ICentro, 'id'> | null): boolean {
    return o1 && o2 ? this.getCentroIdentifier(o1) === this.getCentroIdentifier(o2) : o1 === o2;
  }

  addCentroToCollectionIfMissing<Type extends Pick<ICentro, 'id'>>(
    centroCollection: Type[],
    ...centrosToCheck: (Type | null | undefined)[]
  ): Type[] {
    const centros: Type[] = centrosToCheck.filter(isPresent);
    if (centros.length > 0) {
      const centroCollectionIdentifiers = centroCollection.map(centroItem => this.getCentroIdentifier(centroItem)!);
      const centrosToAdd = centros.filter(centroItem => {
        const centroIdentifier = this.getCentroIdentifier(centroItem);
        if (centroCollectionIdentifiers.includes(centroIdentifier)) {
          return false;
        }
        centroCollectionIdentifiers.push(centroIdentifier);
        return true;
      });
      return [...centrosToAdd, ...centroCollection];
    }
    return centroCollection;
  }

  protected convertDateFromClient<T extends ICentro | NewCentro | PartialUpdateCentro>(centro: T): RestOf<T> {
    return {
      ...centro,
      createdDate: centro.createdDate?.toJSON() ?? null,
      lastModifiedDate: centro.lastModifiedDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restCentro: RestCentro): ICentro {
    return {
      ...restCentro,
      createdDate: restCentro.createdDate ? dayjs(restCentro.createdDate) : undefined,
      lastModifiedDate: restCentro.lastModifiedDate ? dayjs(restCentro.lastModifiedDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestCentro>): HttpResponse<ICentro> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestCentro[]>): HttpResponse<ICentro[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
