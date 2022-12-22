import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { TekComponent } from './list/tek.component';
import { TekDetailComponent } from './detail/tek-detail.component';
import { TekUpdateComponent } from './update/tek-update.component';
import { TekDeleteDialogComponent } from './delete/tek-delete-dialog.component';
import { TekRoutingModule } from './route/tek-routing.module';

@NgModule({
  imports: [SharedModule, TekRoutingModule],
  declarations: [TekComponent, TekDetailComponent, TekUpdateComponent, TekDeleteDialogComponent],
})
export class TekModule {}
