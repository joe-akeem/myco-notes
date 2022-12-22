import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { SpeciesComponent } from '../list/species.component';
import { SpeciesDetailComponent } from '../detail/species-detail.component';
import { SpeciesUpdateComponent } from '../update/species-update.component';
import { SpeciesRoutingResolveService } from './species-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const speciesRoute: Routes = [
  {
    path: '',
    component: SpeciesComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SpeciesDetailComponent,
    resolve: {
      species: SpeciesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SpeciesUpdateComponent,
    resolve: {
      species: SpeciesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SpeciesUpdateComponent,
    resolve: {
      species: SpeciesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(speciesRoute)],
  exports: [RouterModule],
})
export class SpeciesRoutingModule {}
