import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../tek.test-samples';

import { TekFormService } from './tek-form.service';

describe('Tek Form Service', () => {
  let service: TekFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TekFormService);
  });

  describe('Service methods', () => {
    describe('createTekFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTekFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            title: expect.any(Object),
            description: expect.any(Object),
          })
        );
      });

      it('passing ITek should create a new form with FormGroup', () => {
        const formGroup = service.createTekFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            title: expect.any(Object),
            description: expect.any(Object),
          })
        );
      });
    });

    describe('getTek', () => {
      it('should return NewTek for default Tek initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createTekFormGroup(sampleWithNewData);

        const tek = service.getTek(formGroup) as any;

        expect(tek).toMatchObject(sampleWithNewData);
      });

      it('should return NewTek for empty Tek initial value', () => {
        const formGroup = service.createTekFormGroup();

        const tek = service.getTek(formGroup) as any;

        expect(tek).toMatchObject({});
      });

      it('should return ITek', () => {
        const formGroup = service.createTekFormGroup(sampleWithRequiredData);

        const tek = service.getTek(formGroup) as any;

        expect(tek).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITek should not enable id FormControl', () => {
        const formGroup = service.createTekFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTek should disable id FormControl', () => {
        const formGroup = service.createTekFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
