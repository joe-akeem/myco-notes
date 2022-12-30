import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ExperimentComponent } from './list/experiment.component';
import { ExperimentDetailComponent } from './detail/experiment-detail.component';
import { ExperimentUpdateComponent } from './update/experiment-update.component';
import { ExperimentDeleteDialogComponent } from './delete/experiment-delete-dialog.component';
import { ExperimentRoutingModule } from './route/experiment-routing.module';
import { ExperimentSankeyComponent } from './experiment-sankey/experiment-sankey.component';
import { GoogleChartsModule } from 'angular-google-charts';

@NgModule({
  imports: [SharedModule, ExperimentRoutingModule, GoogleChartsModule],
  declarations: [
    ExperimentComponent,
    ExperimentDetailComponent,
    ExperimentUpdateComponent,
    ExperimentDeleteDialogComponent,
    ExperimentSankeyComponent,
  ],
})
export class ExperimentModule {}
