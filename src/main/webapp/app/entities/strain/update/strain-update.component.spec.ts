import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { StrainFormService } from './strain-form.service';
import { StrainService } from '../service/strain.service';
import { IStrain } from '../strain.model';
import { ISpecies } from 'app/entities/species/species.model';
import { SpeciesService } from 'app/entities/species/service/species.service';
import { IExperiment } from 'app/entities/experiment/experiment.model';
import { ExperimentService } from 'app/entities/experiment/service/experiment.service';

import { StrainUpdateComponent } from './strain-update.component';

describe('Strain Management Update Component', () => {
  let comp: StrainUpdateComponent;
  let fixture: ComponentFixture<StrainUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let strainFormService: StrainFormService;
  let strainService: StrainService;
  let speciesService: SpeciesService;
  let experimentService: ExperimentService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [StrainUpdateComponent],
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
      .overrideTemplate(StrainUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(StrainUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    strainFormService = TestBed.inject(StrainFormService);
    strainService = TestBed.inject(StrainService);
    speciesService = TestBed.inject(SpeciesService);
    experimentService = TestBed.inject(ExperimentService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Species query and add missing value', () => {
      const strain: IStrain = { id: 456 };
      const species: ISpecies = { id: 41898 };
      strain.species = species;

      const speciesCollection: ISpecies[] = [{ id: 5872 }];
      jest.spyOn(speciesService, 'query').mockReturnValue(of(new HttpResponse({ body: speciesCollection })));
      const additionalSpecies = [species];
      const expectedCollection: ISpecies[] = [...additionalSpecies, ...speciesCollection];
      jest.spyOn(speciesService, 'addSpeciesToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ strain });
      comp.ngOnInit();

      expect(speciesService.query).toHaveBeenCalled();
      expect(speciesService.addSpeciesToCollectionIfMissing).toHaveBeenCalledWith(
        speciesCollection,
        ...additionalSpecies.map(expect.objectContaining)
      );
      expect(comp.speciesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Experiment query and add missing value', () => {
      const strain: IStrain = { id: 456 };
      const origin: IExperiment = { id: 16880 };
      strain.origin = origin;

      const experimentCollection: IExperiment[] = [{ id: 11576 }];
      jest.spyOn(experimentService, 'query').mockReturnValue(of(new HttpResponse({ body: experimentCollection })));
      const additionalExperiments = [origin];
      const expectedCollection: IExperiment[] = [...additionalExperiments, ...experimentCollection];
      jest.spyOn(experimentService, 'addExperimentToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ strain });
      comp.ngOnInit();

      expect(experimentService.query).toHaveBeenCalled();
      expect(experimentService.addExperimentToCollectionIfMissing).toHaveBeenCalledWith(
        experimentCollection,
        ...additionalExperiments.map(expect.objectContaining)
      );
      expect(comp.experimentsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const strain: IStrain = { id: 456 };
      const species: ISpecies = { id: 73811 };
      strain.species = species;
      const origin: IExperiment = { id: 9943 };
      strain.origin = origin;

      activatedRoute.data = of({ strain });
      comp.ngOnInit();

      expect(comp.speciesSharedCollection).toContain(species);
      expect(comp.experimentsSharedCollection).toContain(origin);
      expect(comp.strain).toEqual(strain);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IStrain>>();
      const strain = { id: 123 };
      jest.spyOn(strainFormService, 'getStrain').mockReturnValue(strain);
      jest.spyOn(strainService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ strain });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: strain }));
      saveSubject.complete();

      // THEN
      expect(strainFormService.getStrain).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(strainService.update).toHaveBeenCalledWith(expect.objectContaining(strain));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IStrain>>();
      const strain = { id: 123 };
      jest.spyOn(strainFormService, 'getStrain').mockReturnValue({ id: null });
      jest.spyOn(strainService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ strain: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: strain }));
      saveSubject.complete();

      // THEN
      expect(strainFormService.getStrain).toHaveBeenCalled();
      expect(strainService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IStrain>>();
      const strain = { id: 123 };
      jest.spyOn(strainService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ strain });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(strainService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareSpecies', () => {
      it('Should forward to speciesService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(speciesService, 'compareSpecies');
        comp.compareSpecies(entity, entity2);
        expect(speciesService.compareSpecies).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareExperiment', () => {
      it('Should forward to experimentService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(experimentService, 'compareExperiment');
        comp.compareExperiment(entity, entity2);
        expect(experimentService.compareExperiment).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
