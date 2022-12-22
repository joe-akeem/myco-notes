import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TekFormService } from './tek-form.service';
import { TekService } from '../service/tek.service';
import { ITek } from '../tek.model';

import { TekUpdateComponent } from './tek-update.component';

describe('Tek Management Update Component', () => {
  let comp: TekUpdateComponent;
  let fixture: ComponentFixture<TekUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let tekFormService: TekFormService;
  let tekService: TekService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [TekUpdateComponent],
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
      .overrideTemplate(TekUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TekUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    tekFormService = TestBed.inject(TekFormService);
    tekService = TestBed.inject(TekService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const tek: ITek = { id: 456 };

      activatedRoute.data = of({ tek });
      comp.ngOnInit();

      expect(comp.tek).toEqual(tek);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITek>>();
      const tek = { id: 123 };
      jest.spyOn(tekFormService, 'getTek').mockReturnValue(tek);
      jest.spyOn(tekService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tek });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: tek }));
      saveSubject.complete();

      // THEN
      expect(tekFormService.getTek).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(tekService.update).toHaveBeenCalledWith(expect.objectContaining(tek));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITek>>();
      const tek = { id: 123 };
      jest.spyOn(tekFormService, 'getTek').mockReturnValue({ id: null });
      jest.spyOn(tekService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tek: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: tek }));
      saveSubject.complete();

      // THEN
      expect(tekFormService.getTek).toHaveBeenCalled();
      expect(tekService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITek>>();
      const tek = { id: 123 };
      jest.spyOn(tekService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tek });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(tekService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
