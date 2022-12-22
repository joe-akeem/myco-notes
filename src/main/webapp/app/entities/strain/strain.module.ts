import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { StrainComponent } from './list/strain.component';
import { StrainDetailComponent } from './detail/strain-detail.component';
import { StrainUpdateComponent } from './update/strain-update.component';
import { StrainDeleteDialogComponent } from './delete/strain-delete-dialog.component';
import { StrainRoutingModule } from './route/strain-routing.module';

@NgModule({
  imports: [SharedModule, StrainRoutingModule],
  declarations: [StrainComponent, StrainDetailComponent, StrainUpdateComponent, StrainDeleteDialogComponent],
})
export class StrainModule {}
