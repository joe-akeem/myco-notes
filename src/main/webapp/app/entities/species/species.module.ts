import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { SpeciesComponent } from './list/species.component';
import { SpeciesDetailComponent } from './detail/species-detail.component';
import { SpeciesUpdateComponent } from './update/species-update.component';
import { SpeciesDeleteDialogComponent } from './delete/species-delete-dialog.component';
import { SpeciesRoutingModule } from './route/species-routing.module';

@NgModule({
  imports: [SharedModule, SpeciesRoutingModule],
  declarations: [SpeciesComponent, SpeciesDetailComponent, SpeciesUpdateComponent, SpeciesDeleteDialogComponent],
})
export class SpeciesModule {}
