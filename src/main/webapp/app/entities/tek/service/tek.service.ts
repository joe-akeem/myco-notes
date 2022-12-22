import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { ITek, NewTek } from '../tek.model';

export type PartialUpdateTek = Partial<ITek> & Pick<ITek, 'id'>;

export type EntityResponseType = HttpResponse<ITek>;
export type EntityArrayResponseType = HttpResponse<ITek[]>;

@Injectable({ providedIn: 'root' })
export class TekService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/teks');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/teks');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(tek: NewTek): Observable<EntityResponseType> {
    return this.http.post<ITek>(this.resourceUrl, tek, { observe: 'response' });
  }

  update(tek: ITek): Observable<EntityResponseType> {
    return this.http.put<ITek>(`${this.resourceUrl}/${this.getTekIdentifier(tek)}`, tek, { observe: 'response' });
  }

  partialUpdate(tek: PartialUpdateTek): Observable<EntityResponseType> {
    return this.http.patch<ITek>(`${this.resourceUrl}/${this.getTekIdentifier(tek)}`, tek, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITek>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITek[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITek[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getTekIdentifier(tek: Pick<ITek, 'id'>): number {
    return tek.id;
  }

  compareTek(o1: Pick<ITek, 'id'> | null, o2: Pick<ITek, 'id'> | null): boolean {
    return o1 && o2 ? this.getTekIdentifier(o1) === this.getTekIdentifier(o2) : o1 === o2;
  }

  addTekToCollectionIfMissing<Type extends Pick<ITek, 'id'>>(tekCollection: Type[], ...teksToCheck: (Type | null | undefined)[]): Type[] {
    const teks: Type[] = teksToCheck.filter(isPresent);
    if (teks.length > 0) {
      const tekCollectionIdentifiers = tekCollection.map(tekItem => this.getTekIdentifier(tekItem)!);
      const teksToAdd = teks.filter(tekItem => {
        const tekIdentifier = this.getTekIdentifier(tekItem);
        if (tekCollectionIdentifiers.includes(tekIdentifier)) {
          return false;
        }
        tekCollectionIdentifiers.push(tekIdentifier);
        return true;
      });
      return [...teksToAdd, ...tekCollection];
    }
    return tekCollection;
  }
}
