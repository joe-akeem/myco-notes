import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../strain.test-samples';

import { StrainFormService } from './strain-form.service';

describe('Strain Form Service', () => {
  let service: StrainFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(StrainFormService);
  });

  describe('Service methods', () => {
    describe('createStrainFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createStrainFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            isolatedAt: expect.any(Object),
            fruiting: expect.any(Object),
            species: expect.any(Object),
            origin: expect.any(Object),
            experiments: expect.any(Object),
          })
        );
      });

      it('passing IStrain should create a new form with FormGroup', () => {
        const formGroup = service.createStrainFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            isolatedAt: expect.any(Object),
            fruiting: expect.any(Object),
            species: expect.any(Object),
            origin: expect.any(Object),
            experiments: expect.any(Object),
          })
        );
      });
    });

    describe('getStrain', () => {
      it('should return NewStrain for default Strain initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createStrainFormGroup(sampleWithNewData);

        const strain = service.getStrain(formGroup) as any;

        expect(strain).toMatchObject(sampleWithNewData);
      });

      it('should return NewStrain for empty Strain initial value', () => {
        const formGroup = service.createStrainFormGroup();

        const strain = service.getStrain(formGroup) as any;

        expect(strain).toMatchObject({});
      });

      it('should return IStrain', () => {
        const formGroup = service.createStrainFormGroup(sampleWithRequiredData);

        const strain = service.getStrain(formGroup) as any;

        expect(strain).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IStrain should not enable id FormControl', () => {
        const formGroup = service.createStrainFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewStrain should disable id FormControl', () => {
        const formGroup = service.createStrainFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
