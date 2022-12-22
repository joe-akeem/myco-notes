import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IStrain } from '../strain.model';
import { StrainService } from '../service/strain.service';

@Injectable({ providedIn: 'root' })
export class StrainRoutingResolveService implements Resolve<IStrain | null> {
  constructor(protected service: StrainService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IStrain | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((strain: HttpResponse<IStrain>) => {
          if (strain.body) {
            return of(strain.body);
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
