import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ExperimentFormService, ExperimentFormGroup } from './experiment-form.service';
import { IExperiment } from '../experiment.model';
import { ExperimentService } from '../service/experiment.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { ITek } from 'app/entities/tek/tek.model';
import { TekService } from 'app/entities/tek/service/tek.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IStrain } from 'app/entities/strain/strain.model';
import { StrainService } from 'app/entities/strain/service/strain.service';

@Component({
  selector: 'jhi-experiment-update',
  templateUrl: './experiment-update.component.html',
})
export class ExperimentUpdateComponent implements OnInit {
  isSaving = false;
  experiment: IExperiment | null = null;

  teksSharedCollection: ITek[] = [];
  usersSharedCollection: IUser[] = [];
  strainsSharedCollection: IStrain[] = [];
  experimentsSharedCollection: IExperiment[] = [];

  editForm: ExperimentFormGroup = this.experimentFormService.createExperimentFormGroup();

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected experimentService: ExperimentService,
    protected experimentFormService: ExperimentFormService,
    protected tekService: TekService,
    protected userService: UserService,
    protected strainService: StrainService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareTek = (o1: ITek | null, o2: ITek | null): boolean => this.tekService.compareTek(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareStrain = (o1: IStrain | null, o2: IStrain | null): boolean => this.strainService.compareStrain(o1, o2);

  compareExperiment = (o1: IExperiment | null, o2: IExperiment | null): boolean => this.experimentService.compareExperiment(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ experiment }) => {
      this.experiment = experiment;
      if (experiment) {
        this.updateForm(experiment);
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
    const experiment = this.experimentFormService.getExperiment(this.editForm);
    if (experiment.id !== null) {
      this.subscribeToSaveResponse(this.experimentService.update(experiment));
    } else {
      this.subscribeToSaveResponse(this.experimentService.create(experiment));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IExperiment>>): void {
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

  protected updateForm(experiment: IExperiment): void {
    this.experiment = experiment;
    this.experimentFormService.resetForm(this.editForm, experiment);

    this.teksSharedCollection = this.tekService.addTekToCollectionIfMissing<ITek>(this.teksSharedCollection, experiment.tek);
    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, experiment.conductedBy);
    this.strainsSharedCollection = this.strainService.addStrainToCollectionIfMissing<IStrain>(
      this.strainsSharedCollection,
      ...(experiment.involvedStrains ?? [])
    );
    this.experimentsSharedCollection = this.experimentService.addExperimentToCollectionIfMissing<IExperiment>(
      this.experimentsSharedCollection,
      ...(experiment.precedingExperiments ?? [])
    );
  }

  protected loadRelationshipsOptions(): void {
    this.tekService
      .query()
      .pipe(map((res: HttpResponse<ITek[]>) => res.body ?? []))
      .pipe(map((teks: ITek[]) => this.tekService.addTekToCollectionIfMissing<ITek>(teks, this.experiment?.tek)))
      .subscribe((teks: ITek[]) => (this.teksSharedCollection = teks));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.experiment?.conductedBy)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.strainService
      .query()
      .pipe(map((res: HttpResponse<IStrain[]>) => res.body ?? []))
      .pipe(
        map((strains: IStrain[]) =>
          this.strainService.addStrainToCollectionIfMissing<IStrain>(strains, ...(this.experiment?.involvedStrains ?? []))
        )
      )
      .subscribe((strains: IStrain[]) => (this.strainsSharedCollection = strains));

    this.experimentService
      .query()
      .pipe(map((res: HttpResponse<IExperiment[]>) => res.body ?? []))
      .pipe(
        map((experiments: IExperiment[]) =>
          this.experimentService.addExperimentToCollectionIfMissing<IExperiment>(
            experiments,
            ...(this.experiment?.precedingExperiments ?? [])
          )
        )
      )
      .subscribe((experiments: IExperiment[]) => (this.experimentsSharedCollection = experiments));
  }
}
