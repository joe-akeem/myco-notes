import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { SpeciesFormService } from './species-form.service';
import { SpeciesService } from '../service/species.service';
import { ISpecies } from '../species.model';
import { IGenus } from 'app/entities/genus/genus.model';
import { GenusService } from 'app/entities/genus/service/genus.service';

import { SpeciesUpdateComponent } from './species-update.component';

describe('Species Management Update Component', () => {
  let comp: SpeciesUpdateComponent;
  let fixture: ComponentFixture<SpeciesUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let speciesFormService: SpeciesFormService;
  let speciesService: SpeciesService;
  let genusService: GenusService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [SpeciesUpdateComponent],
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
      .overrideTemplate(SpeciesUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SpeciesUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    speciesFormService = TestBed.inject(SpeciesFormService);
    speciesService = TestBed.inject(SpeciesService);
    genusService = TestBed.inject(GenusService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Genus query and add missing value', () => {
      const species: ISpecies = { id: 456 };
      const genus: IGenus = { id: 95547 };
      species.genus = genus;

      const genusCollection: IGenus[] = [{ id: 29226 }];
      jest.spyOn(genusService, 'query').mockReturnValue(of(new HttpResponse({ body: genusCollection })));
      const additionalGenera = [genus];
      const expectedCollection: IGenus[] = [...additionalGenera, ...genusCollection];
      jest.spyOn(genusService, 'addGenusToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ species });
      comp.ngOnInit();

      expect(genusService.query).toHaveBeenCalled();
      expect(genusService.addGenusToCollectionIfMissing).toHaveBeenCalledWith(
        genusCollection,
        ...additionalGenera.map(expect.objectContaining)
      );
      expect(comp.generaSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const species: ISpecies = { id: 456 };
      const genus: IGenus = { id: 82487 };
      species.genus = genus;

      activatedRoute.data = of({ species });
      comp.ngOnInit();

      expect(comp.generaSharedCollection).toContain(genus);
      expect(comp.species).toEqual(species);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISpecies>>();
      const species = { id: 123 };
      jest.spyOn(speciesFormService, 'getSpecies').mockReturnValue(species);
      jest.spyOn(speciesService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ species });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: species }));
      saveSubject.complete();

      // THEN
      expect(speciesFormService.getSpecies).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(speciesService.update).toHaveBeenCalledWith(expect.objectContaining(species));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISpecies>>();
      const species = { id: 123 };
      jest.spyOn(speciesFormService, 'getSpecies').mockReturnValue({ id: null });
      jest.spyOn(speciesService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ species: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: species }));
      saveSubject.complete();

      // THEN
      expect(speciesFormService.getSpecies).toHaveBeenCalled();
      expect(speciesService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISpecies>>();
      const species = { id: 123 };
      jest.spyOn(speciesService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ species });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(speciesService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareGenus', () => {
      it('Should forward to genusService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(genusService, 'compareGenus');
        comp.compareGenus(entity, entity2);
        expect(genusService.compareGenus).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
