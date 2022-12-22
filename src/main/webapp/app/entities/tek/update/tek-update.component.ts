import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { TekFormService, TekFormGroup } from './tek-form.service';
import { ITek } from '../tek.model';
import { TekService } from '../service/tek.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-tek-update',
  templateUrl: './tek-update.component.html',
})
export class TekUpdateComponent implements OnInit {
  isSaving = false;
  tek: ITek | null = null;

  editForm: TekFormGroup = this.tekFormService.createTekFormGroup();

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected tekService: TekService,
    protected tekFormService: TekFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ tek }) => {
      this.tek = tek;
      if (tek) {
        this.updateForm(tek);
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
    const tek = this.tekFormService.getTek(this.editForm);
    if (tek.id !== null) {
      this.subscribeToSaveResponse(this.tekService.update(tek));
    } else {
      this.subscribeToSaveResponse(this.tekService.create(tek));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITek>>): void {
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

  protected updateForm(tek: ITek): void {
    this.tek = tek;
    this.tekFormService.resetForm(this.editForm, tek);
  }
}
