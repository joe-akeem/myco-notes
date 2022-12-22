import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { StrainComponent } from '../list/strain.component';
import { StrainDetailComponent } from '../detail/strain-detail.component';
import { StrainUpdateComponent } from '../update/strain-update.component';
import { StrainRoutingResolveService } from './strain-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const strainRoute: Routes = [
  {
    path: '',
    component: StrainComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: StrainDetailComponent,
    resolve: {
      strain: StrainRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: StrainUpdateComponent,
    resolve: {
      strain: StrainRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: StrainUpdateComponent,
    resolve: {
      strain: StrainRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(strainRoute)],
  exports: [RouterModule],
})
export class StrainRoutingModule {}
