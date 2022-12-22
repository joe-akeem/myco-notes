import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { GenusFormService } from './genus-form.service';
import { GenusService } from '../service/genus.service';
import { IGenus } from '../genus.model';

import { GenusUpdateComponent } from './genus-update.component';

describe('Genus Management Update Component', () => {
  let comp: GenusUpdateComponent;
  let fixture: ComponentFixture<GenusUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let genusFormService: GenusFormService;
  let genusService: GenusService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [GenusUpdateComponent],
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
      .overrideTemplate(GenusUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(GenusUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    genusFormService = TestBed.inject(GenusFormService);
    genusService = TestBed.inject(GenusService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const genus: IGenus = { id: 456 };

      activatedRoute.data = of({ genus });
      comp.ngOnInit();

      expect(comp.genus).toEqual(genus);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IGenus>>();
      const genus = { id: 123 };
      jest.spyOn(genusFormService, 'getGenus').mockReturnValue(genus);
      jest.spyOn(genusService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ genus });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: genus }));
      saveSubject.complete();

      // THEN
      expect(genusFormService.getGenus).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(genusService.update).toHaveBeenCalledWith(expect.objectContaining(genus));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IGenus>>();
      const genus = { id: 123 };
      jest.spyOn(genusFormService, 'getGenus').mockReturnValue({ id: null });
      jest.spyOn(genusService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ genus: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: genus }));
      saveSubject.complete();

      // THEN
      expect(genusFormService.getGenus).toHaveBeenCalled();
      expect(genusService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IGenus>>();
      const genus = { id: 123 };
      jest.spyOn(genusService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ genus });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(genusService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
