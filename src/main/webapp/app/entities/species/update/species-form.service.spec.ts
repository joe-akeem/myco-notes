import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../species.test-samples';

import { SpeciesFormService } from './species-form.service';

describe('Species Form Service', () => {
  let service: SpeciesFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SpeciesFormService);
  });

  describe('Service methods', () => {
    describe('createSpeciesFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createSpeciesFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            commonName: expect.any(Object),
            description: expect.any(Object),
            genus: expect.any(Object),
          })
        );
      });

      it('passing ISpecies should create a new form with FormGroup', () => {
        const formGroup = service.createSpeciesFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            commonName: expect.any(Object),
            description: expect.any(Object),
            genus: expect.any(Object),
          })
        );
      });
    });

    describe('getSpecies', () => {
      it('should return NewSpecies for default Species initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createSpeciesFormGroup(sampleWithNewData);

        const species = service.getSpecies(formGroup) as any;

        expect(species).toMatchObject(sampleWithNewData);
      });

      it('should return NewSpecies for empty Species initial value', () => {
        const formGroup = service.createSpeciesFormGroup();

        const species = service.getSpecies(formGroup) as any;

        expect(species).toMatchObject({});
      });

      it('should return ISpecies', () => {
        const formGroup = service.createSpeciesFormGroup(sampleWithRequiredData);

        const species = service.getSpecies(formGroup) as any;

        expect(species).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ISpecies should not enable id FormControl', () => {
        const formGroup = service.createSpeciesFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewSpecies should disable id FormControl', () => {
        const formGroup = service.createSpeciesFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
