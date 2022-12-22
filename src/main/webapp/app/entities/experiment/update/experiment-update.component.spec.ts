import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ExperimentFormService } from './experiment-form.service';
import { ExperimentService } from '../service/experiment.service';
import { IExperiment } from '../experiment.model';
import { ITek } from 'app/entities/tek/tek.model';
import { TekService } from 'app/entities/tek/service/tek.service';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IStrain } from 'app/entities/strain/strain.model';
import { StrainService } from 'app/entities/strain/service/strain.service';

import { ExperimentUpdateComponent } from './experiment-update.component';

describe('Experiment Management Update Component', () => {
  let comp: ExperimentUpdateComponent;
  let fixture: ComponentFixture<ExperimentUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let experimentFormService: ExperimentFormService;
  let experimentService: ExperimentService;
  let tekService: TekService;
  let userService: UserService;
  let strainService: StrainService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ExperimentUpdateComponent],
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
      .overrideTemplate(ExperimentUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ExperimentUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    experimentFormService = TestBed.inject(ExperimentFormService);
    experimentService = TestBed.inject(ExperimentService);
    tekService = TestBed.inject(TekService);
    userService = TestBed.inject(UserService);
    strainService = TestBed.inject(StrainService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Tek query and add missing value', () => {
      const experiment: IExperiment = { id: 456 };
      const tek: ITek = { id: 32039 };
      experiment.tek = tek;

      const tekCollection: ITek[] = [{ id: 8270 }];
      jest.spyOn(tekService, 'query').mockReturnValue(of(new HttpResponse({ body: tekCollection })));
      const additionalTeks = [tek];
      const expectedCollection: ITek[] = [...additionalTeks, ...tekCollection];
      jest.spyOn(tekService, 'addTekToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ experiment });
      comp.ngOnInit();

      expect(tekService.query).toHaveBeenCalled();
      expect(tekService.addTekToCollectionIfMissing).toHaveBeenCalledWith(tekCollection, ...additionalTeks.map(expect.objectContaining));
      expect(comp.teksSharedCollection).toEqual(expectedCollection);
    });

    it('Should call User query and add missing value', () => {
      const experiment: IExperiment = { id: 456 };
      const conductedBy: IUser = { id: 45160 };
      experiment.conductedBy = conductedBy;

      const userCollection: IUser[] = [{ id: 28184 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [conductedBy];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ experiment });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Strain query and add missing value', () => {
      const experiment: IExperiment = { id: 456 };
      const involvedStrains: IStrain[] = [{ id: 20949 }];
      experiment.involvedStrains = involvedStrains;

      const strainCollection: IStrain[] = [{ id: 16482 }];
      jest.spyOn(strainService, 'query').mockReturnValue(of(new HttpResponse({ body: strainCollection })));
      const additionalStrains = [...involvedStrains];
      const expectedCollection: IStrain[] = [...additionalStrains, ...strainCollection];
      jest.spyOn(strainService, 'addStrainToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ experiment });
      comp.ngOnInit();

      expect(strainService.query).toHaveBeenCalled();
      expect(strainService.addStrainToCollectionIfMissing).toHaveBeenCalledWith(
        strainCollection,
        ...additionalStrains.map(expect.objectContaining)
      );
      expect(comp.strainsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Experiment query and add missing value', () => {
      const experiment: IExperiment = { id: 456 };
      const precedingExperiments: IExperiment[] = [{ id: 24932 }];
      experiment.precedingExperiments = precedingExperiments;

      const experimentCollection: IExperiment[] = [{ id: 78821 }];
      jest.spyOn(experimentService, 'query').mockReturnValue(of(new HttpResponse({ body: experimentCollection })));
      const additionalExperiments = [...precedingExperiments];
      const expectedCollection: IExperiment[] = [...additionalExperiments, ...experimentCollection];
      jest.spyOn(experimentService, 'addExperimentToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ experiment });
      comp.ngOnInit();

      expect(experimentService.query).toHaveBeenCalled();
      expect(experimentService.addExperimentToCollectionIfMissing).toHaveBeenCalledWith(
        experimentCollection,
        ...additionalExperiments.map(expect.objectContaining)
      );
      expect(comp.experimentsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const experiment: IExperiment = { id: 456 };
      const tek: ITek = { id: 46900 };
      experiment.tek = tek;
      const conductedBy: IUser = { id: 48600 };
      experiment.conductedBy = conductedBy;
      const involvedStrains: IStrain = { id: 81971 };
      experiment.involvedStrains = [involvedStrains];
      const precedingExperiments: IExperiment = { id: 95590 };
      experiment.precedingExperiments = [precedingExperiments];

      activatedRoute.data = of({ experiment });
      comp.ngOnInit();

      expect(comp.teksSharedCollection).toContain(tek);
      expect(comp.usersSharedCollection).toContain(conductedBy);
      expect(comp.strainsSharedCollection).toContain(involvedStrains);
      expect(comp.experimentsSharedCollection).toContain(precedingExperiments);
      expect(comp.experiment).toEqual(experiment);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IExperiment>>();
      const experiment = { id: 123 };
      jest.spyOn(experimentFormService, 'getExperiment').mockReturnValue(experiment);
      jest.spyOn(experimentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ experiment });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: experiment }));
      saveSubject.complete();

      // THEN
      expect(experimentFormService.getExperiment).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(experimentService.update).toHaveBeenCalledWith(expect.objectContaining(experiment));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IExperiment>>();
      const experiment = { id: 123 };
      jest.spyOn(experimentFormService, 'getExperiment').mockReturnValue({ id: null });
      jest.spyOn(experimentService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ experiment: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: experiment }));
      saveSubject.complete();

      // THEN
      expect(experimentFormService.getExperiment).toHaveBeenCalled();
      expect(experimentService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IExperiment>>();
      const experiment = { id: 123 };
      jest.spyOn(experimentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ experiment });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(experimentService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareTek', () => {
      it('Should forward to tekService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(tekService, 'compareTek');
        comp.compareTek(entity, entity2);
        expect(tekService.compareTek).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareUser', () => {
      it('Should forward to userService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
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
