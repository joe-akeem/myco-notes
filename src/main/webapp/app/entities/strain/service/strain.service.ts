import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IStrain, NewStrain } from '../strain.model';

export type PartialUpdateStrain = Partial<IStrain> & Pick<IStrain, 'id'>;

type RestOf<T extends IStrain | NewStrain> = Omit<T, 'isolatedAt'> & {
  isolatedAt?: string | null;
};

export type RestStrain = RestOf<IStrain>;

export type NewRestStrain = RestOf<NewStrain>;

export type PartialUpdateRestStrain = RestOf<PartialUpdateStrain>;

export type EntityResponseType = HttpResponse<IStrain>;
export type EntityArrayResponseType = HttpResponse<IStrain[]>;

@Injectable({ providedIn: 'root' })
export class StrainService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/strains');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/strains');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(strain: NewStrain): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(strain);
    return this.http
      .post<RestStrain>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(strain: IStrain): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(strain);
    return this.http
      .put<RestStrain>(`${this.resourceUrl}/${this.getStrainIdentifier(strain)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(strain: PartialUpdateStrain): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(strain);
    return this.http
      .patch<RestStrain>(`${this.resourceUrl}/${this.getStrainIdentifier(strain)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestStrain>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestStrain[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestStrain[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  getStrainIdentifier(strain: Pick<IStrain, 'id'>): number {
    return strain.id;
  }

  compareStrain(o1: Pick<IStrain, 'id'> | null, o2: Pick<IStrain, 'id'> | null): boolean {
    return o1 && o2 ? this.getStrainIdentifier(o1) === this.getStrainIdentifier(o2) : o1 === o2;
  }

  addStrainToCollectionIfMissing<Type extends Pick<IStrain, 'id'>>(
    strainCollection: Type[],
    ...strainsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const strains: Type[] = strainsToCheck.filter(isPresent);
    if (strains.length > 0) {
      const strainCollectionIdentifiers = strainCollection.map(strainItem => this.getStrainIdentifier(strainItem)!);
      const strainsToAdd = strains.filter(strainItem => {
        const strainIdentifier = this.getStrainIdentifier(strainItem);
        if (strainCollectionIdentifiers.includes(strainIdentifier)) {
          return false;
        }
        strainCollectionIdentifiers.push(strainIdentifier);
        return true;
      });
      return [...strainsToAdd, ...strainCollection];
    }
    return strainCollection;
  }

  protected convertDateFromClient<T extends IStrain | NewStrain | PartialUpdateStrain>(strain: T): RestOf<T> {
    return {
      ...strain,
      isolatedAt: strain.isolatedAt?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restStrain: RestStrain): IStrain {
    return {
      ...restStrain,
      isolatedAt: restStrain.isolatedAt ? dayjs(restStrain.isolatedAt) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestStrain>): HttpResponse<IStrain> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestStrain[]>): HttpResponse<IStrain[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
