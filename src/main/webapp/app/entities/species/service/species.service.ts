import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { ISpecies, NewSpecies } from '../species.model';

export type PartialUpdateSpecies = Partial<ISpecies> & Pick<ISpecies, 'id'>;

export type EntityResponseType = HttpResponse<ISpecies>;
export type EntityArrayResponseType = HttpResponse<ISpecies[]>;

@Injectable({ providedIn: 'root' })
export class SpeciesService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/species');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/species');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(species: NewSpecies): Observable<EntityResponseType> {
    return this.http.post<ISpecies>(this.resourceUrl, species, { observe: 'response' });
  }

  update(species: ISpecies): Observable<EntityResponseType> {
    return this.http.put<ISpecies>(`${this.resourceUrl}/${this.getSpeciesIdentifier(species)}`, species, { observe: 'response' });
  }

  partialUpdate(species: PartialUpdateSpecies): Observable<EntityResponseType> {
    return this.http.patch<ISpecies>(`${this.resourceUrl}/${this.getSpeciesIdentifier(species)}`, species, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISpecies>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISpecies[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISpecies[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getSpeciesIdentifier(species: Pick<ISpecies, 'id'>): number {
    return species.id;
  }

  compareSpecies(o1: Pick<ISpecies, 'id'> | null, o2: Pick<ISpecies, 'id'> | null): boolean {
    return o1 && o2 ? this.getSpeciesIdentifier(o1) === this.getSpeciesIdentifier(o2) : o1 === o2;
  }

  addSpeciesToCollectionIfMissing<Type extends Pick<ISpecies, 'id'>>(
    speciesCollection: Type[],
    ...speciesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const species: Type[] = speciesToCheck.filter(isPresent);
    if (species.length > 0) {
      const speciesCollectionIdentifiers = speciesCollection.map(speciesItem => this.getSpeciesIdentifier(speciesItem)!);
      const speciesToAdd = species.filter(speciesItem => {
        const speciesIdentifier = this.getSpeciesIdentifier(speciesItem);
        if (speciesCollectionIdentifiers.includes(speciesIdentifier)) {
          return false;
        }
        speciesCollectionIdentifiers.push(speciesIdentifier);
        return true;
      });
      return [...speciesToAdd, ...speciesCollection];
    }
    return speciesCollection;
  }
}
