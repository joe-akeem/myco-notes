import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IExperiment } from '../experiment.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-experiment-detail',
  templateUrl: './experiment-detail.component.html',
})
export class ExperimentDetailComponent implements OnInit {
  experiment: IExperiment | null = null;
  data = [
    { Framework: 'Vue', Stars: '166443', Released: '2014' },
    { Framework: 'React', Stars: '150793', Released: '2013' },
    { Framework: 'Angular', Stars: '62342', Released: '2016' },
    { Framework: 'Backbone', Stars: '27647', Released: '2010' },
    { Framework: 'Ember', Stars: '21471', Released: '2011' },
  ];

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ experiment }) => {
      this.experiment = experiment;
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
