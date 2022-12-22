import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IGenus, NewGenus } from '../genus.model';

export type PartialUpdateGenus = Partial<IGenus> & Pick<IGenus, 'id'>;

export type EntityResponseType = HttpResponse<IGenus>;
export type EntityArrayResponseType = HttpResponse<IGenus[]>;

@Injectable({ providedIn: 'root' })
export class GenusService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/genera');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/genera');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(genus: NewGenus): Observable<EntityResponseType> {
    return this.http.post<IGenus>(this.resourceUrl, genus, { observe: 'response' });
  }

  update(genus: IGenus): Observable<EntityResponseType> {
    return this.http.put<IGenus>(`${this.resourceUrl}/${this.getGenusIdentifier(genus)}`, genus, { observe: 'response' });
  }

  partialUpdate(genus: PartialUpdateGenus): Observable<EntityResponseType> {
    return this.http.patch<IGenus>(`${this.resourceUrl}/${this.getGenusIdentifier(genus)}`, genus, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IGenus>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IGenus[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IGenus[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getGenusIdentifier(genus: Pick<IGenus, 'id'>): number {
    return genus.id;
  }

  compareGenus(o1: Pick<IGenus, 'id'> | null, o2: Pick<IGenus, 'id'> | null): boolean {
    return o1 && o2 ? this.getGenusIdentifier(o1) === this.getGenusIdentifier(o2) : o1 === o2;
  }

  addGenusToCollectionIfMissing<Type extends Pick<IGenus, 'id'>>(
    genusCollection: Type[],
    ...generaToCheck: (Type | null | undefined)[]
  ): Type[] {
    const genera: Type[] = generaToCheck.filter(isPresent);
    if (genera.length > 0) {
      const genusCollectionIdentifiers = genusCollection.map(genusItem => this.getGenusIdentifier(genusItem)!);
      const generaToAdd = genera.filter(genusItem => {
        const genusIdentifier = this.getGenusIdentifier(genusItem);
        if (genusCollectionIdentifiers.includes(genusIdentifier)) {
          return false;
        }
        genusCollectionIdentifiers.push(genusIdentifier);
        return true;
      });
      return [...generaToAdd, ...genusCollection];
    }
    return genusCollection;
  }
}
