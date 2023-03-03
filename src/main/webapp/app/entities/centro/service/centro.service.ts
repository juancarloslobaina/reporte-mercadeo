import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICentro, NewCentro } from '../centro.model';

export type PartialUpdateCentro = Partial<ICentro> & Pick<ICentro, 'id'>;

export type EntityResponseType = HttpResponse<ICentro>;
export type EntityArrayResponseType = HttpResponse<ICentro[]>;

@Injectable({ providedIn: 'root' })
export class CentroService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/centros');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(centro: NewCentro): Observable<EntityResponseType> {
    return this.http.post<ICentro>(this.resourceUrl, centro, { observe: 'response' });
  }

  update(centro: ICentro): Observable<EntityResponseType> {
    return this.http.put<ICentro>(`${this.resourceUrl}/${this.getCentroIdentifier(centro)}`, centro, { observe: 'response' });
  }

  partialUpdate(centro: PartialUpdateCentro): Observable<EntityResponseType> {
    return this.http.patch<ICentro>(`${this.resourceUrl}/${this.getCentroIdentifier(centro)}`, centro, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICentro>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICentro[]>(this.resourceUrl, { params: options, observe: 'response' });
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
}
