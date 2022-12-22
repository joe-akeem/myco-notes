import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IExperiment } from '../experiment.model';
import { ExperimentService } from '../service/experiment.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './experiment-delete-dialog.component.html',
})
export class ExperimentDeleteDialogComponent {
  experiment?: IExperiment;

  constructor(protected experimentService: ExperimentService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.experimentService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
