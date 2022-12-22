import { Component, OnInit, ElementRef } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ImageFormService, ImageFormGroup } from './image-form.service';
import { IImage } from '../image.model';
import { ImageService } from '../service/image.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IObservation } from 'app/entities/observation/observation.model';
import { ObservationService } from 'app/entities/observation/service/observation.service';
import { IStrain } from 'app/entities/strain/strain.model';
import { StrainService } from 'app/entities/strain/service/strain.service';
import { ITek } from 'app/entities/tek/tek.model';
import { TekService } from 'app/entities/tek/service/tek.service';

@Component({
  selector: 'jhi-image-update',
  templateUrl: './image-update.component.html',
})
export class ImageUpdateComponent implements OnInit {
  isSaving = false;
  image: IImage | null = null;

  observationsSharedCollection: IObservation[] = [];
  strainsSharedCollection: IStrain[] = [];
  teksSharedCollection: ITek[] = [];

  editForm: ImageFormGroup = this.imageFormService.createImageFormGroup();

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected imageService: ImageService,
    protected imageFormService: ImageFormService,
    protected observationService: ObservationService,
    protected strainService: StrainService,
    protected tekService: TekService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareObservation = (o1: IObservation | null, o2: IObservation | null): boolean => this.observationService.compareObservation(o1, o2);

  compareStrain = (o1: IStrain | null, o2: IStrain | null): boolean => this.strainService.compareStrain(o1, o2);

  compareTek = (o1: ITek | null, o2: ITek | null): boolean => this.tekService.compareTek(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ image }) => {
      this.image = image;
      if (image) {
        this.updateForm(image);
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

  clearInputImage(field: string, fieldContentType: string, idInput: string): void {
    this.editForm.patchValue({
      [field]: null,
      [fieldContentType]: null,
    });
    if (idInput && this.elementRef.nativeElement.querySelector('#' + idInput)) {
      this.elementRef.nativeElement.querySelector('#' + idInput).value = null;
    }
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const image = this.imageFormService.getImage(this.editForm);
    if (image.id !== null) {
      this.subscribeToSaveResponse(this.imageService.update(image));
    } else {
      this.subscribeToSaveResponse(this.imageService.create(image));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IImage>>): void {
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

  protected updateForm(image: IImage): void {
    this.image = image;
    this.imageFormService.resetForm(this.editForm, image);

    this.observationsSharedCollection = this.observationService.addObservationToCollectionIfMissing<IObservation>(
      this.observationsSharedCollection,
      image.observation
    );
    this.strainsSharedCollection = this.strainService.addStrainToCollectionIfMissing<IStrain>(this.strainsSharedCollection, image.strain);
    this.teksSharedCollection = this.tekService.addTekToCollectionIfMissing<ITek>(this.teksSharedCollection, image.tek);
  }

  protected loadRelationshipsOptions(): void {
    this.observationService
      .query()
      .pipe(map((res: HttpResponse<IObservation[]>) => res.body ?? []))
      .pipe(
        map((observations: IObservation[]) =>
          this.observationService.addObservationToCollectionIfMissing<IObservation>(observations, this.image?.observation)
        )
      )
      .subscribe((observations: IObservation[]) => (this.observationsSharedCollection = observations));

    this.strainService
      .query()
      .pipe(map((res: HttpResponse<IStrain[]>) => res.body ?? []))
      .pipe(map((strains: IStrain[]) => this.strainService.addStrainToCollectionIfMissing<IStrain>(strains, this.image?.strain)))
      .subscribe((strains: IStrain[]) => (this.strainsSharedCollection = strains));

    this.tekService
      .query()
      .pipe(map((res: HttpResponse<ITek[]>) => res.body ?? []))
      .pipe(map((teks: ITek[]) => this.tekService.addTekToCollectionIfMissing<ITek>(teks, this.image?.tek)))
      .subscribe((teks: ITek[]) => (this.teksSharedCollection = teks));
  }
}
