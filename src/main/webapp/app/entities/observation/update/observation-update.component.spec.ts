import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ObservationFormService } from './observation-form.service';
import { ObservationService } from '../service/observation.service';
import { IObservation } from '../observation.model';
import { IExperiment } from 'app/entities/experiment/experiment.model';
import { ExperimentService } from 'app/entities/experiment/service/experiment.service';

import { ObservationUpdateComponent } from './observation-update.component';

describe('Observation Management Update Component', () => {
  let comp: ObservationUpdateComponent;
  let fixture: ComponentFixture<ObservationUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let observationFormService: ObservationFormService;
  let observationService: ObservationService;
  let experimentService: ExperimentService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ObservationUpdateComponent],
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
      .overrideTemplate(ObservationUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ObservationUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    observationFormService = TestBed.inject(ObservationFormService);
    observationService = TestBed.inject(ObservationService);
    experimentService = TestBed.inject(ExperimentService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Experiment query and add missing value', () => {
      const observation: IObservation = { id: 456 };
      const experiment: IExperiment = { id: 17500 };
      observation.experiment = experiment;

      const experimentCollection: IExperiment[] = [{ id: 7467 }];
      jest.spyOn(experimentService, 'query').mockReturnValue(of(new HttpResponse({ body: experimentCollection })));
      const additionalExperiments = [experiment];
      const expectedCollection: IExperiment[] = [...additionalExperiments, ...experimentCollection];
      jest.spyOn(experimentService, 'addExperimentToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ observation });
      comp.ngOnInit();

      expect(experimentService.query).toHaveBeenCalled();
      expect(experimentService.addExperimentToCollectionIfMissing).toHaveBeenCalledWith(
        experimentCollection,
        ...additionalExperiments.map(expect.objectContaining)
      );
      expect(comp.experimentsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const observation: IObservation = { id: 456 };
      const experiment: IExperiment = { id: 81926 };
      observation.experiment = experiment;

      activatedRoute.data = of({ observation });
      comp.ngOnInit();

      expect(comp.experimentsSharedCollection).toContain(experiment);
      expect(comp.observation).toEqual(observation);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IObservation>>();
      const observation = { id: 123 };
      jest.spyOn(observationFormService, 'getObservation').mockReturnValue(observation);
      jest.spyOn(observationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ observation });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: observation }));
      saveSubject.complete();

      // THEN
      expect(observationFormService.getObservation).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(observationService.update).toHaveBeenCalledWith(expect.objectContaining(observation));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IObservation>>();
      const observation = { id: 123 };
      jest.spyOn(observationFormService, 'getObservation').mockReturnValue({ id: null });
      jest.spyOn(observationService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ observation: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: observation }));
      saveSubject.complete();

      // THEN
      expect(observationFormService.getObservation).toHaveBeenCalled();
      expect(observationService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IObservation>>();
      const observation = { id: 123 };
      jest.spyOn(observationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ observation });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(observationService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
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
