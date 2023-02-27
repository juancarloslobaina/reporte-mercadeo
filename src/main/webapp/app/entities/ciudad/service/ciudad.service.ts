import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICiudad, NewCiudad } from '../ciudad.model';
import { SearchWithPagination } from '../../../core/request/request.model';

export type PartialUpdateCiudad = Partial<ICiudad> & Pick<ICiudad, 'id'>;

export type EntityResponseType = HttpResponse<ICiudad>;
export type EntityArrayResponseType = HttpResponse<ICiudad[]>;

@Injectable({ providedIn: 'root' })
export class CiudadService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/ciudads');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(ciudad: NewCiudad): Observable<EntityResponseType> {
    return this.http.post<ICiudad>(this.resourceUrl, ciudad, { observe: 'response' });
  }

  update(ciudad: ICiudad): Observable<EntityResponseType> {
    return this.http.put<ICiudad>(`${this.resourceUrl}/${this.getCiudadIdentifier(ciudad)}`, ciudad, { observe: 'response' });
  }

  partialUpdate(ciudad: PartialUpdateCiudad): Observable<EntityResponseType> {
    return this.http.patch<ICiudad>(`${this.resourceUrl}/${this.getCiudadIdentifier(ciudad)}`, ciudad, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICiudad>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICiudad[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICiudad[]>(this.resourceUrl, { params: options, observe: 'response' });
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
}
