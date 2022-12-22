import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { SpeciesFormService, SpeciesFormGroup } from './species-form.service';
import { ISpecies } from '../species.model';
import { SpeciesService } from '../service/species.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IGenus } from 'app/entities/genus/genus.model';
import { GenusService } from 'app/entities/genus/service/genus.service';

@Component({
  selector: 'jhi-species-update',
  templateUrl: './species-update.component.html',
})
export class SpeciesUpdateComponent implements OnInit {
  isSaving = false;
  species: ISpecies | null = null;

  generaSharedCollection: IGenus[] = [];

  editForm: SpeciesFormGroup = this.speciesFormService.createSpeciesFormGroup();

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected speciesService: SpeciesService,
    protected speciesFormService: SpeciesFormService,
    protected genusService: GenusService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareGenus = (o1: IGenus | null, o2: IGenus | null): boolean => this.genusService.compareGenus(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ species }) => {
      this.species = species;
      if (species) {
        this.updateForm(species);
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
    const species = this.speciesFormService.getSpecies(this.editForm);
    if (species.id !== null) {
      this.subscribeToSaveResponse(this.speciesService.update(species));
    } else {
      this.subscribeToSaveResponse(this.speciesService.create(species));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISpecies>>): void {
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

  protected updateForm(species: ISpecies): void {
    this.species = species;
    this.speciesFormService.resetForm(this.editForm, species);

    this.generaSharedCollection = this.genusService.addGenusToCollectionIfMissing<IGenus>(this.generaSharedCollection, species.genus);
  }

  protected loadRelationshipsOptions(): void {
    this.genusService
      .query()
      .pipe(map((res: HttpResponse<IGenus[]>) => res.body ?? []))
      .pipe(map((genera: IGenus[]) => this.genusService.addGenusToCollectionIfMissing<IGenus>(genera, this.species?.genus)))
      .subscribe((genera: IGenus[]) => (this.generaSharedCollection = genera));
  }
}
