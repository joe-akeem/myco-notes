import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IObservation } from '../observation.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-observation-detail',
  templateUrl: './observation-detail.component.html',
})
export class ObservationDetailComponent implements OnInit {
  observation: IObservation | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ observation }) => {
      this.observation = observation;
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  previousState(): void {
    window.history.back();
  }
}
