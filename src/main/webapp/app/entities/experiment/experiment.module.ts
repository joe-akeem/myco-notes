import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ExperimentComponent } from './list/experiment.component';
import { ExperimentDetailComponent } from './detail/experiment-detail.component';
import { ExperimentUpdateComponent } from './update/experiment-update.component';
import { ExperimentDeleteDialogComponent } from './delete/experiment-delete-dialog.component';
import { ExperimentRoutingModule } from './route/experiment-routing.module';

@NgModule({
  imports: [SharedModule, ExperimentRoutingModule],
  declarations: [ExperimentComponent, ExperimentDetailComponent, ExperimentUpdateComponent, ExperimentDeleteDialogComponent],
})
export class ExperimentModule {}
