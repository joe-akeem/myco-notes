import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IGenus } from '../genus.model';
import { GenusService } from '../service/genus.service';

@Injectable({ providedIn: 'root' })
export class GenusRoutingResolveService implements Resolve<IGenus | null> {
  constructor(protected service: GenusService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IGenus | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((genus: HttpResponse<IGenus>) => {
          if (genus.body) {
            return of(genus.body);
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
