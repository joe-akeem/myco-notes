import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { InstructionComponent } from '../list/instruction.component';
import { InstructionDetailComponent } from '../detail/instruction-detail.component';
import { InstructionUpdateComponent } from '../update/instruction-update.component';
import { InstructionRoutingResolveService } from './instruction-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const instructionRoute: Routes = [
  {
    path: '',
    component: InstructionComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: InstructionDetailComponent,
    resolve: {
      instruction: InstructionRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: InstructionUpdateComponent,
    resolve: {
      instruction: InstructionRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: InstructionUpdateComponent,
    resolve: {
      instruction: InstructionRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(instructionRoute)],
  exports: [RouterModule],
})
export class InstructionRoutingModule {}
