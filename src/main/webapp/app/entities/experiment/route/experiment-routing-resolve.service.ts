import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IExperiment } from '../experiment.model';
import { ExperimentService } from '../service/experiment.service';

@Injectable({ providedIn: 'root' })
export class ExperimentRoutingResolveService implements Resolve<IExperiment | null> {
  constructor(protected service: ExperimentService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IExperiment | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((experiment: HttpResponse<IExperiment>) => {
          if (experiment.body) {
            return of(experiment.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
