import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ObservationComponent } from '../list/observation.component';
import { ObservationDetailComponent } from '../detail/observation-detail.component';
import { ObservationUpdateComponent } from '../update/observation-update.component';
import { ObservationRoutingResolveService } from './observation-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const observationRoute: Routes = [
  {
    path: '',
    component: ObservationComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ObservationDetailComponent,
    resolve: {
      observation: ObservationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ObservationUpdateComponent,
    resolve: {
      observation: ObservationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ObservationUpdateComponent,
    resolve: {
      observation: ObservationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(observationRoute)],
  exports: [RouterModule],
})
export class ObservationRoutingModule {}
