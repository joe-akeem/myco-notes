import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { GenusComponent } from '../list/genus.component';
import { GenusDetailComponent } from '../detail/genus-detail.component';
import { GenusUpdateComponent } from '../update/genus-update.component';
import { GenusRoutingResolveService } from './genus-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const genusRoute: Routes = [
  {
    path: '',
    component: GenusComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: GenusDetailComponent,
    resolve: {
      genus: GenusRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: GenusUpdateComponent,
    resolve: {
      genus: GenusRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: GenusUpdateComponent,
    resolve: {
      genus: GenusRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(genusRoute)],
  exports: [RouterModule],
})
export class GenusRoutingModule {}
