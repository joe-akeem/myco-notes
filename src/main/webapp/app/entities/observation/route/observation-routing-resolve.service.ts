import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IObservation } from '../observation.model';
import { ObservationService } from '../service/observation.service';

@Injectable({ providedIn: 'root' })
export class ObservationRoutingResolveService implements Resolve<IObservation | null> {
  constructor(protected service: ObservationService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IObservation | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((observation: HttpResponse<IObservation>) => {
          if (observation.body) {
            return of(observation.body);
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
