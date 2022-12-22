import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { GenusFormService, GenusFormGroup } from './genus-form.service';
import { IGenus } from '../genus.model';
import { GenusService } from '../service/genus.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-genus-update',
  templateUrl: './genus-update.component.html',
})
export class GenusUpdateComponent implements OnInit {
  isSaving = false;
  genus: IGenus | null = null;

  editForm: GenusFormGroup = this.genusFormService.createGenusFormGroup();

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected genusService: GenusService,
    protected genusFormService: GenusFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ genus }) => {
      this.genus = genus;
      if (genus) {
        this.updateForm(genus);
      }
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
    const genus = this.genusFormService.getGenus(this.editForm);
    if (genus.id !== null) {
      this.subscribeToSaveResponse(this.genusService.update(genus));
    } else {
      this.subscribeToSaveResponse(this.genusService.create(genus));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IGenus>>): void {
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

  protected updateForm(genus: IGenus): void {
    this.genus = genus;
    this.genusFormService.resetForm(this.editForm, genus);
  }
}
