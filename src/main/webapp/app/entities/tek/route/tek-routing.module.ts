import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TekComponent } from '../list/tek.component';
import { TekDetailComponent } from '../detail/tek-detail.component';
import { TekUpdateComponent } from '../update/tek-update.component';
import { TekRoutingResolveService } from './tek-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const tekRoute: Routes = [
  {
    path: '',
    component: TekComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TekDetailComponent,
    resolve: {
      tek: TekRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TekUpdateComponent,
    resolve: {
      tek: TekRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TekUpdateComponent,
    resolve: {
      tek: TekRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(tekRoute)],
  exports: [RouterModule],
})
export class TekRoutingModule {}
