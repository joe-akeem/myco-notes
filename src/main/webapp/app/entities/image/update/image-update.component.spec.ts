import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ImageFormService } from './image-form.service';
import { ImageService } from '../service/image.service';
import { IImage } from '../image.model';
import { IObservation } from 'app/entities/observation/observation.model';
import { ObservationService } from 'app/entities/observation/service/observation.service';
import { IStrain } from 'app/entities/strain/strain.model';
import { StrainService } from 'app/entities/strain/service/strain.service';
import { ITek } from 'app/entities/tek/tek.model';
import { TekService } from 'app/entities/tek/service/tek.service';

import { ImageUpdateComponent } from './image-update.component';

describe('Image Management Update Component', () => {
  let comp: ImageUpdateComponent;
  let fixture: ComponentFixture<ImageUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let imageFormService: ImageFormService;
  let imageService: ImageService;
  let observationService: ObservationService;
  let strainService: StrainService;
  let tekService: TekService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ImageUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(ImageUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ImageUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    imageFormService = TestBed.inject(ImageFormService);
    imageService = TestBed.inject(ImageService);
    observationService = TestBed.inject(ObservationService);
    strainService = TestBed.inject(StrainService);
    tekService = TestBed.inject(TekService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Observation query and add missing value', () => {
      const image: IImage = { id: 456 };
      const observation: IObservation = { id: 67277 };
      image.observation = observation;

      const observationCollection: IObservation[] = [{ id: 55687 }];
      jest.spyOn(observationService, 'query').mockReturnValue(of(new HttpResponse({ body: observationCollection })));
      const additionalObservations = [observation];
      const expectedCollection: IObservation[] = [...additionalObservations, ...observationCollection];
      jest.spyOn(observationService, 'addObservationToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ image });
      comp.ngOnInit();

      expect(observationService.query).toHaveBeenCalled();
      expect(observationService.addObservationToCollectionIfMissing).toHaveBeenCalledWith(
        observationCollection,
        ...additionalObservations.map(expect.objectContaining)
      );
      expect(comp.observationsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Strain query and add missing value', () => {
      const image: IImage = { id: 456 };
      const strain: IStrain = { id: 25861 };
      image.strain = strain;

      const strainCollection: IStrain[] = [{ id: 98476 }];
      jest.spyOn(strainService, 'query').mockReturnValue(of(new HttpResponse({ body: strainCollection })));
      const additionalStrains = [strain];
      const expectedCollection: IStrain[] = [...additionalStrains, ...strainCollection];
      jest.spyOn(strainService, 'addStrainToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ image });
      comp.ngOnInit();

      expect(strainService.query).toHaveBeenCalled();
      expect(strainService.addStrainToCollectionIfMissing).toHaveBeenCalledWith(
        strainCollection,
        ...additionalStrains.map(expect.objectContaining)
      );
      expect(comp.strainsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Tek query and add missing value', () => {
      const image: IImage = { id: 456 };
      const tek: ITek = { id: 56095 };
      image.tek = tek;

      const tekCollection: ITek[] = [{ id: 86079 }];
      jest.spyOn(tekService, 'query').mockReturnValue(of(new HttpResponse({ body: tekCollection })));
      const additionalTeks = [tek];
      const expectedCollection: ITek[] = [...additionalTeks, ...tekCollection];
      jest.spyOn(tekService, 'addTekToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ image });
      comp.ngOnInit();

      expect(tekService.query).toHaveBeenCalled();
      expect(tekService.addTekToCollectionIfMissing).toHaveBeenCalledWith(tekCollection, ...additionalTeks.map(expect.objectContaining));
      expect(comp.teksSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const image: IImage = { id: 456 };
      const observation: IObservation = { id: 48148 };
      image.observation = observation;
      const strain: IStrain = { id: 37821 };
      image.strain = strain;
      const tek: ITek = { id: 61203 };
      image.tek = tek;

      activatedRoute.data = of({ image });
      comp.ngOnInit();

      expect(comp.observationsSharedCollection).toContain(observation);
      expect(comp.strainsSharedCollection).toContain(strain);
      expect(comp.teksSharedCollection).toContain(tek);
      expect(comp.image).toEqual(image);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IImage>>();
      const image = { id: 123 };
      jest.spyOn(imageFormService, 'getImage').mockReturnValue(image);
      jest.spyOn(imageService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ image });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: image }));
      saveSubject.complete();

      // THEN
      expect(imageFormService.getImage).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(imageService.update).toHaveBeenCalledWith(expect.objectContaining(image));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IImage>>();
      const image = { id: 123 };
      jest.spyOn(imageFormService, 'getImage').mockReturnValue({ id: null });
      jest.spyOn(imageService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ image: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: image }));
      saveSubject.complete();

      // THEN
      expect(imageFormService.getImage).toHaveBeenCalled();
      expect(imageService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IImage>>();
      const image = { id: 123 };
      jest.spyOn(imageService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ image });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(imageService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareObservation', () => {
      it('Should forward to observationService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(observationService, 'compareObservation');
        comp.compareObservation(entity, entity2);
        expect(observationService.compareObservation).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareStrain', () => {
      it('Should forward to strainService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(strainService, 'compareStrain');
        comp.compareStrain(entity, entity2);
        expect(strainService.compareStrain).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareTek', () => {
      it('Should forward to tekService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(tekService, 'compareTek');
        comp.compareTek(entity, entity2);
        expect(tekService.compareTek).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
