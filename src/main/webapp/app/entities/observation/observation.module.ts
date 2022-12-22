import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ObservationComponent } from './list/observation.component';
import { ObservationDetailComponent } from './detail/observation-detail.component';
import { ObservationUpdateComponent } from './update/observation-update.component';
import { ObservationDeleteDialogComponent } from './delete/observation-delete-dialog.component';
import { ObservationRoutingModule } from './route/observation-routing.module';

@NgModule({
  imports: [SharedModule, ObservationRoutingModule],
  declarations: [ObservationComponent, ObservationDetailComponent, ObservationUpdateComponent, ObservationDeleteDialogComponent],
})
export class ObservationModule {}
