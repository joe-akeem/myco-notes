import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITek } from '../tek.model';
import { TekService } from '../service/tek.service';

@Injectable({ providedIn: 'root' })
export class TekRoutingResolveService implements Resolve<ITek | null> {
  constructor(protected service: TekService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITek | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((tek: HttpResponse<ITek>) => {
          if (tek.body) {
            return of(tek.body);
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
