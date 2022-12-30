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
import { SankeyRow, IExperiment, NewExperiment } from '../experiment.model';
import { Row } from 'angular-google-charts';

export type PartialUpdateExperiment = Partial<IExperiment> & Pick<IExperiment, 'id'>;

type RestOf<T extends IExperiment | NewExperiment> = Omit<T, 'conductedAt'> & {
  conductedAt?: string | null;
};

export type RestExperiment = RestOf<IExperiment>;

export type NewRestExperiment = RestOf<NewExperiment>;

export type PartialUpdateRestExperiment = RestOf<PartialUpdateExperiment>;

export type EntityResponseType = HttpResponse<IExperiment>;
export type EntityArrayResponseType = HttpResponse<IExperiment[]>;

@Injectable({ providedIn: 'root' })
export class ExperimentService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/experiments');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/experiments');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(experiment: NewExperiment): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(experiment);
    return this.http
      .post<RestExperiment>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(experiment: IExperiment): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(experiment);
    return this.http
      .put<RestExperiment>(`${this.resourceUrl}/${this.getExperimentIdentifier(experiment)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(experiment: PartialUpdateExperiment): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(experiment);
    return this.http
      .patch<RestExperiment>(`${this.resourceUrl}/${this.getExperimentIdentifier(experiment)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestExperiment>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  getSankeyCartData(id: number): Observable<HttpResponse<SankeyRow[]>> {
    return this.http.get<SankeyRow[]>(`${this.resourceUrl}/${id}/sankeyChartData`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestExperiment[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestExperiment[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  getExperimentIdentifier(experiment: Pick<IExperiment, 'id'>): number {
    return experiment.id;
  }

  compareExperiment(o1: Pick<IExperiment, 'id'> | null, o2: Pick<IExperiment, 'id'> | null): boolean {
    return o1 && o2 ? this.getExperimentIdentifier(o1) === this.getExperimentIdentifier(o2) : o1 === o2;
  }

  addExperimentToCollectionIfMissing<Type extends Pick<IExperiment, 'id'>>(
    experimentCollection: Type[],
    ...experimentsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const experiments: Type[] = experimentsToCheck.filter(isPresent);
    if (experiments.length > 0) {
      const experimentCollectionIdentifiers = experimentCollection.map(experimentItem => this.getExperimentIdentifier(experimentItem)!);
      const experimentsToAdd = experiments.filter(experimentItem => {
        const experimentIdentifier = this.getExperimentIdentifier(experimentItem);
        if (experimentCollectionIdentifiers.includes(experimentIdentifier)) {
          return false;
        }
        experimentCollectionIdentifiers.push(experimentIdentifier);
        return true;
      });
      return [...experimentsToAdd, ...experimentCollection];
    }
    return experimentCollection;
  }

  protected convertDateFromClient<T extends IExperiment | NewExperiment | PartialUpdateExperiment>(experiment: T): RestOf<T> {
    return {
      ...experiment,
      conductedAt: experiment.conductedAt?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restExperiment: RestExperiment): IExperiment {
    return {
      ...restExperiment,
      conductedAt: restExperiment.conductedAt ? dayjs(restExperiment.conductedAt) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestExperiment>): HttpResponse<IExperiment> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestExperiment[]>): HttpResponse<IExperiment[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
