import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../experiment.test-samples';

import { ExperimentFormService } from './experiment-form.service';

describe('Experiment Form Service', () => {
  let service: ExperimentFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ExperimentFormService);
  });

  describe('Service methods', () => {
    describe('createExperimentFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createExperimentFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            title: expect.any(Object),
            notes: expect.any(Object),
            conductedAt: expect.any(Object),
            tek: expect.any(Object),
            conductedBy: expect.any(Object),
            involvedStrains: expect.any(Object),
            precedingExperiments: expect.any(Object),
            followupExperiments: expect.any(Object),
          })
        );
      });

      it('passing IExperiment should create a new form with FormGroup', () => {
        const formGroup = service.createExperimentFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            title: expect.any(Object),
            notes: expect.any(Object),
            conductedAt: expect.any(Object),
            tek: expect.any(Object),
            conductedBy: expect.any(Object),
            involvedStrains: expect.any(Object),
            precedingExperiments: expect.any(Object),
            followupExperiments: expect.any(Object),
          })
        );
      });
    });

    describe('getExperiment', () => {
      it('should return NewExperiment for default Experiment initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createExperimentFormGroup(sampleWithNewData);

        const experiment = service.getExperiment(formGroup) as any;

        expect(experiment).toMatchObject(sampleWithNewData);
      });

      it('should return NewExperiment for empty Experiment initial value', () => {
        const formGroup = service.createExperimentFormGroup();

        const experiment = service.getExperiment(formGroup) as any;

        expect(experiment).toMatchObject({});
      });

      it('should return IExperiment', () => {
        const formGroup = service.createExperimentFormGroup(sampleWithRequiredData);

        const experiment = service.getExperiment(formGroup) as any;

        expect(experiment).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IExperiment should not enable id FormControl', () => {
        const formGroup = service.createExperimentFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewExperiment should disable id FormControl', () => {
        const formGroup = service.createExperimentFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
