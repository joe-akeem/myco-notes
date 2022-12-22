import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ExperimentComponent } from '../list/experiment.component';
import { ExperimentDetailComponent } from '../detail/experiment-detail.component';
import { ExperimentUpdateComponent } from '../update/experiment-update.component';
import { ExperimentRoutingResolveService } from './experiment-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const experimentRoute: Routes = [
  {
    path: '',
    component: ExperimentComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ExperimentDetailComponent,
    resolve: {
      experiment: ExperimentRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ExperimentUpdateComponent,
    resolve: {
      experiment: ExperimentRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ExperimentUpdateComponent,
    resolve: {
      experiment: ExperimentRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(experimentRoute)],
  exports: [RouterModule],
})
export class ExperimentRoutingModule {}
