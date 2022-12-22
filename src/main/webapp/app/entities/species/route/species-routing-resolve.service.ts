import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISpecies } from '../species.model';
import { SpeciesService } from '../service/species.service';

@Injectable({ providedIn: 'root' })
export class SpeciesRoutingResolveService implements Resolve<ISpecies | null> {
  constructor(protected service: SpeciesService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ISpecies | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((species: HttpResponse<ISpecies>) => {
          if (species.body) {
            return of(species.body);
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
