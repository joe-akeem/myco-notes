import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { StrainFormService, StrainFormGroup } from './strain-form.service';
import { IStrain } from '../strain.model';
import { StrainService } from '../service/strain.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { ISpecies } from 'app/entities/species/species.model';
import { SpeciesService } from 'app/entities/species/service/species.service';
import { IExperiment } from 'app/entities/experiment/experiment.model';
import { ExperimentService } from 'app/entities/experiment/service/experiment.service';

@Component({
  selector: 'jhi-strain-update',
  templateUrl: './strain-update.component.html',
})
export class StrainUpdateComponent implements OnInit {
  isSaving = false;
  strain: IStrain | null = null;

  speciesSharedCollection: ISpecies[] = [];
  experimentsSharedCollection: IExperiment[] = [];

  editForm: StrainFormGroup = this.strainFormService.createStrainFormGroup();

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected strainService: StrainService,
    protected strainFormService: StrainFormService,
    protected speciesService: SpeciesService,
    protected experimentService: ExperimentService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareSpecies = (o1: ISpecies | null, o2: ISpecies | null): boolean => this.speciesService.compareSpecies(o1, o2);

  compareExperiment = (o1: IExperiment | null, o2: IExperiment | null): boolean => this.experimentService.compareExperiment(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ strain }) => {
      this.strain = strain;
      if (strain) {
        this.updateForm(strain);
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
    const strain = this.strainFormService.getStrain(this.editForm);
    if (strain.id !== null) {
      this.subscribeToSaveResponse(this.strainService.update(strain));
    } else {
      this.subscribeToSaveResponse(this.strainService.create(strain));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IStrain>>): void {
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

  protected updateForm(strain: IStrain): void {
    this.strain = strain;
    this.strainFormService.resetForm(this.editForm, strain);

    this.speciesSharedCollection = this.speciesService.addSpeciesToCollectionIfMissing<ISpecies>(
      this.speciesSharedCollection,
      strain.species
    );
    this.experimentsSharedCollection = this.experimentService.addExperimentToCollectionIfMissing<IExperiment>(
      this.experimentsSharedCollection,
      strain.origin
    );
  }

  protected loadRelationshipsOptions(): void {
    this.speciesService
      .query()
      .pipe(map((res: HttpResponse<ISpecies[]>) => res.body ?? []))
      .pipe(map((species: ISpecies[]) => this.speciesService.addSpeciesToCollectionIfMissing<ISpecies>(species, this.strain?.species)))
      .subscribe((species: ISpecies[]) => (this.speciesSharedCollection = species));

    this.experimentService
      .query()
      .pipe(map((res: HttpResponse<IExperiment[]>) => res.body ?? []))
      .pipe(
        map((experiments: IExperiment[]) =>
          this.experimentService.addExperimentToCollectionIfMissing<IExperiment>(experiments, this.strain?.origin)
        )
      )
      .subscribe((experiments: IExperiment[]) => (this.experimentsSharedCollection = experiments));
  }
}
