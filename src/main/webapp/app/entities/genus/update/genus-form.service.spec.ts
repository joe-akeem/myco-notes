import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../genus.test-samples';

import { GenusFormService } from './genus-form.service';

describe('Genus Form Service', () => {
  let service: GenusFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GenusFormService);
  });

  describe('Service methods', () => {
    describe('createGenusFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createGenusFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            commonName: expect.any(Object),
            description: expect.any(Object),
          })
        );
      });

      it('passing IGenus should create a new form with FormGroup', () => {
        const formGroup = service.createGenusFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            commonName: expect.any(Object),
            description: expect.any(Object),
          })
        );
      });
    });

    describe('getGenus', () => {
      it('should return NewGenus for default Genus initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createGenusFormGroup(sampleWithNewData);

        const genus = service.getGenus(formGroup) as any;

        expect(genus).toMatchObject(sampleWithNewData);
      });

      it('should return NewGenus for empty Genus initial value', () => {
        const formGroup = service.createGenusFormGroup();

        const genus = service.getGenus(formGroup) as any;

        expect(genus).toMatchObject({});
      });

      it('should return IGenus', () => {
        const formGroup = service.createGenusFormGroup(sampleWithRequiredData);

        const genus = service.getGenus(formGroup) as any;

        expect(genus).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IGenus should not enable id FormControl', () => {
        const formGroup = service.createGenusFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewGenus should disable id FormControl', () => {
        const formGroup = service.createGenusFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
