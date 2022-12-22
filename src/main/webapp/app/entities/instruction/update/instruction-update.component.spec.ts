import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { InstructionFormService } from './instruction-form.service';
import { InstructionService } from '../service/instruction.service';
import { IInstruction } from '../instruction.model';
import { ITek } from 'app/entities/tek/tek.model';
import { TekService } from 'app/entities/tek/service/tek.service';

import { InstructionUpdateComponent } from './instruction-update.component';

describe('Instruction Management Update Component', () => {
  let comp: InstructionUpdateComponent;
  let fixture: ComponentFixture<InstructionUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let instructionFormService: InstructionFormService;
  let instructionService: InstructionService;
  let tekService: TekService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [InstructionUpdateComponent],
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
      .overrideTemplate(InstructionUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(InstructionUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    instructionFormService = TestBed.inject(InstructionFormService);
    instructionService = TestBed.inject(InstructionService);
    tekService = TestBed.inject(TekService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Tek query and add missing value', () => {
      const instruction: IInstruction = { id: 456 };
      const instructionSet: ITek = { id: 19658 };
      instruction.instructionSet = instructionSet;

      const tekCollection: ITek[] = [{ id: 27308 }];
      jest.spyOn(tekService, 'query').mockReturnValue(of(new HttpResponse({ body: tekCollection })));
      const additionalTeks = [instructionSet];
      const expectedCollection: ITek[] = [...additionalTeks, ...tekCollection];
      jest.spyOn(tekService, 'addTekToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ instruction });
      comp.ngOnInit();

      expect(tekService.query).toHaveBeenCalled();
      expect(tekService.addTekToCollectionIfMissing).toHaveBeenCalledWith(tekCollection, ...additionalTeks.map(expect.objectContaining));
      expect(comp.teksSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const instruction: IInstruction = { id: 456 };
      const instructionSet: ITek = { id: 70215 };
      instruction.instructionSet = instructionSet;

      activatedRoute.data = of({ instruction });
      comp.ngOnInit();

      expect(comp.teksSharedCollection).toContain(instructionSet);
      expect(comp.instruction).toEqual(instruction);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IInstruction>>();
      const instruction = { id: 123 };
      jest.spyOn(instructionFormService, 'getInstruction').mockReturnValue(instruction);
      jest.spyOn(instructionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ instruction });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: instruction }));
      saveSubject.complete();

      // THEN
      expect(instructionFormService.getInstruction).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(instructionService.update).toHaveBeenCalledWith(expect.objectContaining(instruction));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IInstruction>>();
      const instruction = { id: 123 };
      jest.spyOn(instructionFormService, 'getInstruction').mockReturnValue({ id: null });
      jest.spyOn(instructionService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ instruction: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: instruction }));
      saveSubject.complete();

      // THEN
      expect(instructionFormService.getInstruction).toHaveBeenCalled();
      expect(instructionService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IInstruction>>();
      const instruction = { id: 123 };
      jest.spyOn(instructionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ instruction });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(instructionService.update).toHaveBeenCalled();
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
  });
});
