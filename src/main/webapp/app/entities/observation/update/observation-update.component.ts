import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ObservationFormService, ObservationFormGroup } from './observation-form.service';
import { IObservation } from '../observation.model';
import { ObservationService } from '../service/observation.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IExperiment } from 'app/entities/experiment/experiment.model';
import { ExperimentService } from 'app/entities/experiment/service/experiment.service';

@Component({
  selector: 'jhi-observation-update',
  templateUrl: './observation-update.component.html',
})
export class ObservationUpdateComponent implements OnInit {
  isSaving = false;
  observation: IObservation | null = null;

  experimentsSharedCollection: IExperiment[] = [];

  editForm: ObservationFormGroup = this.observationFormService.createObservationFormGroup();

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected observationService: ObservationService,
    protected observationFormService: ObservationFormService,
    protected experimentService: ExperimentService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareExperiment = (o1: IExperiment | null, o2: IExperiment | null): boolean => this.experimentService.compareExperiment(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ observation }) => {
      this.observation = observation;
      if (observation) {
        this.updateForm(observation);
      }

      this.loadRelationshipsOptions();
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('mycoNotesApp.error', { ...err, key: 'error.file.' + err.key })),
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const observation = this.observationFormService.getObservation(this.editForm);
    if (observation.id !== null) {
      this.subscribeToSaveResponse(this.observationService.update(observation));
    } else {
      this.subscribeToSaveResponse(this.observationService.create(observation));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IObservation>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(observation: IObservation): void {
    this.observation = observation;
    this.observationFormService.resetForm(this.editForm, observation);

    this.experimentsSharedCollection = this.experimentService.addExperimentToCollectionIfMissing<IExperiment>(
      this.experimentsSharedCollection,
      observation.experiment
    );
  }

  protected loadRelationshipsOptions(): void {
    this.experimentService
      .query()
      .pipe(map((res: HttpResponse<IExperiment[]>) => res.body ?? []))
      .pipe(
        map((experiments: IExperiment[]) =>
          this.experimentService.addExperimentToCollectionIfMissing<IExperiment>(experiments, this.observation?.experiment)
        )
      )
      .subscribe((experiments: IExperiment[]) => (this.experimentsSharedCollection = experiments));
  }
}
