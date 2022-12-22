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
import { IObservation, NewObservation } from '../observation.model';

export type PartialUpdateObservation = Partial<IObservation> & Pick<IObservation, 'id'>;

type RestOf<T extends IObservation | NewObservation> = Omit<T, 'observationDate'> & {
  observationDate?: string | null;
};

export type RestObservation = RestOf<IObservation>;

export type NewRestObservation = RestOf<NewObservation>;

export type PartialUpdateRestObservation = RestOf<PartialUpdateObservation>;

export type EntityResponseType = HttpResponse<IObservation>;
export type EntityArrayResponseType = HttpResponse<IObservation[]>;

@Injectable({ providedIn: 'root' })
export class ObservationService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/observations');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/observations');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(observation: NewObservation): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(observation);
    return this.http
      .post<RestObservation>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(observation: IObservation): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(observation);
    return this.http
      .put<RestObservation>(`${this.resourceUrl}/${this.getObservationIdentifier(observation)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(observation: PartialUpdateObservation): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(observation);
    return this.http
      .patch<RestObservation>(`${this.resourceUrl}/${this.getObservationIdentifier(observation)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestObservation>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestObservation[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestObservation[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  getObservationIdentifier(observation: Pick<IObservation, 'id'>): number {
    return observation.id;
  }

  compareObservation(o1: Pick<IObservation, 'id'> | null, o2: Pick<IObservation, 'id'> | null): boolean {
    return o1 && o2 ? this.getObservationIdentifier(o1) === this.getObservationIdentifier(o2) : o1 === o2;
  }

  addObservationToCollectionIfMissing<Type extends Pick<IObservation, 'id'>>(
    observationCollection: Type[],
    ...observationsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const observations: Type[] = observationsToCheck.filter(isPresent);
    if (observations.length > 0) {
      const observationCollectionIdentifiers = observationCollection.map(
        observationItem => this.getObservationIdentifier(observationItem)!
      );
      const observationsToAdd = observations.filter(observationItem => {
        const observationIdentifier = this.getObservationIdentifier(observationItem);
        if (observationCollectionIdentifiers.includes(observationIdentifier)) {
          return false;
        }
        observationCollectionIdentifiers.push(observationIdentifier);
        return true;
      });
      return [...observationsToAdd, ...observationCollection];
    }
    return observationCollection;
  }

  protected convertDateFromClient<T extends IObservation | NewObservation | PartialUpdateObservation>(observation: T): RestOf<T> {
    return {
      ...observation,
      observationDate: observation.observationDate?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restObservation: RestObservation): IObservation {
    return {
      ...restObservation,
      observationDate: restObservation.observationDate ? dayjs(restObservation.observationDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestObservation>): HttpResponse<IObservation> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestObservation[]>): HttpResponse<IObservation[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
