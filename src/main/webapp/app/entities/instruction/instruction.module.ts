import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { InstructionComponent } from './list/instruction.component';
import { InstructionDetailComponent } from './detail/instruction-detail.component';
import { InstructionUpdateComponent } from './update/instruction-update.component';
import { InstructionDeleteDialogComponent } from './delete/instruction-delete-dialog.component';
import { InstructionRoutingModule } from './route/instruction-routing.module';

@NgModule({
  imports: [SharedModule, InstructionRoutingModule],
  declarations: [InstructionComponent, InstructionDetailComponent, InstructionUpdateComponent, InstructionDeleteDialogComponent],
})
export class InstructionModule {}
