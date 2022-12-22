import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { GenusComponent } from './list/genus.component';
import { GenusDetailComponent } from './detail/genus-detail.component';
import { GenusUpdateComponent } from './update/genus-update.component';
import { GenusDeleteDialogComponent } from './delete/genus-delete-dialog.component';
import { GenusRoutingModule } from './route/genus-routing.module';

@NgModule({
  imports: [SharedModule, GenusRoutingModule],
  declarations: [GenusComponent, GenusDetailComponent, GenusUpdateComponent, GenusDeleteDialogComponent],
})
export class GenusModule {}
