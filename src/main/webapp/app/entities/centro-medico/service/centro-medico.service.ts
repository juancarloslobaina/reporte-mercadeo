import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICentroMedico, NewCentroMedico } from '../centro-medico.model';

export type PartialUpdateCentroMedico = Partial<ICentroMedico> & Pick<ICentroMedico, 'id'>;

export type EntityResponseType = HttpResponse<ICentroMedico>;
export type EntityArrayResponseType = HttpResponse<ICentroMedico[]>;

@Injectable({ providedIn: 'root' })
export class CentroMedicoService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/centro-medicos');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(centroMedico: NewCentroMedico): Observable<EntityResponseType> {
    return this.http.post<ICentroMedico>(this.resourceUrl, centroMedico, { observe: 'response' });
  }

  update(centroMedico: ICentroMedico): Observable<EntityResponseType> {
    return this.http.put<ICentroMedico>(`${this.resourceUrl}/${this.getCentroMedicoIdentifier(centroMedico)}`, centroMedico, {
      observe: 'response',
    });
  }

  partialUpdate(centroMedico: PartialUpdateCentroMedico): Observable<EntityResponseType> {
    return this.http.patch<ICentroMedico>(`${this.resourceUrl}/${this.getCentroMedicoIdentifier(centroMedico)}`, centroMedico, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICentroMedico>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICentroMedico[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getCentroMedicoIdentifier(centroMedico: Pick<ICentroMedico, 'id'>): number {
    return centroMedico.id;
  }

  compareCentroMedico(o1: Pick<ICentroMedico, 'id'> | null, o2: Pick<ICentroMedico, 'id'> | null): boolean {
    return o1 && o2 ? this.getCentroMedicoIdentifier(o1) === this.getCentroMedicoIdentifier(o2) : o1 === o2;
  }

  addCentroMedicoToCollectionIfMissing<Type extends Pick<ICentroMedico, 'id'>>(
    centroMedicoCollection: Type[],
    ...centroMedicosToCheck: (Type | null | undefined)[]
  ): Type[] {
    const centroMedicos: Type[] = centroMedicosToCheck.filter(isPresent);
    if (centroMedicos.length > 0) {
      const centroMedicoCollectionIdentifiers = centroMedicoCollection.map(
        centroMedicoItem => this.getCentroMedicoIdentifier(centroMedicoItem)!
      );
      const centroMedicosToAdd = centroMedicos.filter(centroMedicoItem => {
        const centroMedicoIdentifier = this.getCentroMedicoIdentifier(centroMedicoItem);
        if (centroMedicoCollectionIdentifiers.includes(centroMedicoIdentifier)) {
          return false;
        }
        centroMedicoCollectionIdentifiers.push(centroMedicoIdentifier);
        return true;
      });
      return [...centroMedicosToAdd, ...centroMedicoCollection];
    }
    return centroMedicoCollection;
  }
}
