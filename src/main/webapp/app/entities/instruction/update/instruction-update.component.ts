import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { InstructionFormService, InstructionFormGroup } from './instruction-form.service';
import { IInstruction } from '../instruction.model';
import { InstructionService } from '../service/instruction.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { ITek } from 'app/entities/tek/tek.model';
import { TekService } from 'app/entities/tek/service/tek.service';

@Component({
  selector: 'jhi-instruction-update',
  templateUrl: './instruction-update.component.html',
})
export class InstructionUpdateComponent implements OnInit {
  isSaving = false;
  instruction: IInstruction | null = null;

  teksSharedCollection: ITek[] = [];

  editForm: InstructionFormGroup = this.instructionFormService.createInstructionFormGroup();

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected instructionService: InstructionService,
    protected instructionFormService: InstructionFormService,
    protected tekService: TekService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareTek = (o1: ITek | null, o2: ITek | null): boolean => this.tekService.compareTek(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ instruction }) => {
      this.instruction = instruction;
      if (instruction) {
        this.updateForm(instruction);
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
    const instruction = this.instructionFormService.getInstruction(this.editForm);
    if (instruction.id !== null) {
      this.subscribeToSaveResponse(this.instructionService.update(instruction));
    } else {
      this.subscribeToSaveResponse(this.instructionService.create(instruction));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IInstruction>>): void {
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

  protected updateForm(instruction: IInstruction): void {
    this.instruction = instruction;
    this.instructionFormService.resetForm(this.editForm, instruction);

    this.teksSharedCollection = this.tekService.addTekToCollectionIfMissing<ITek>(this.teksSharedCollection, instruction.instructionSet);
  }

  protected loadRelationshipsOptions(): void {
    this.tekService
      .query()
      .pipe(map((res: HttpResponse<ITek[]>) => res.body ?? []))
      .pipe(map((teks: ITek[]) => this.tekService.addTekToCollectionIfMissing<ITek>(teks, this.instruction?.instructionSet)))
      .subscribe((teks: ITek[]) => (this.teksSharedCollection = teks));
  }
}
