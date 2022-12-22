import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IExperiment, NewExperiment } from '../experiment.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IExperiment for edit and NewExperimentFormGroupInput for create.
 */
type ExperimentFormGroupInput = IExperiment | PartialWithRequiredKeyOf<NewExperiment>;

type ExperimentFormDefaults = Pick<NewExperiment, 'id' | 'involvedStrains' | 'precedingExperiments' | 'followupExperiments'>;

type ExperimentFormGroupContent = {
  id: FormControl<IExperiment['id'] | NewExperiment['id']>;
  title: FormControl<IExperiment['title']>;
  notes: FormControl<IExperiment['notes']>;
  conductedAt: FormControl<IExperiment['conductedAt']>;
  tek: FormControl<IExperiment['tek']>;
  conductedBy: FormControl<IExperiment['conductedBy']>;
  involvedStrains: FormControl<IExperiment['involvedStrains']>;
  precedingExperiments: FormControl<IExperiment['precedingExperiments']>;
  followupExperiments: FormControl<IExperiment['followupExperiments']>;
};

export type ExperimentFormGroup = FormGroup<ExperimentFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ExperimentFormService {
  createExperimentFormGroup(experiment: ExperimentFormGroupInput = { id: null }): ExperimentFormGroup {
    const experimentRawValue = {
      ...this.getFormDefaults(),
      ...experiment,
    };
    return new FormGroup<ExperimentFormGroupContent>({
      id: new FormControl(
        { value: experimentRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      title: new FormControl(experimentRawValue.title, {
        validators: [Validators.required],
      }),
      notes: new FormControl(experimentRawValue.notes),
      conductedAt: new FormControl(experimentRawValue.conductedAt, {
        validators: [Validators.required],
      }),
      tek: new FormControl(experimentRawValue.tek, {
        validators: [Validators.required],
      }),
      conductedBy: new FormControl(experimentRawValue.conductedBy, {
        validators: [Validators.required],
      }),
      involvedStrains: new FormControl(experimentRawValue.involvedStrains ?? []),
      precedingExperiments: new FormControl(experimentRawValue.precedingExperiments ?? []),
      followupExperiments: new FormControl(experimentRawValue.followupExperiments ?? []),
    });
  }

  getExperiment(form: ExperimentFormGroup): IExperiment | NewExperiment {
    return form.getRawValue() as IExperiment | NewExperiment;
  }

  resetForm(form: ExperimentFormGroup, experiment: ExperimentFormGroupInput): void {
    const experimentRawValue = { ...this.getFormDefaults(), ...experiment };
    form.reset(
      {
        ...experimentRawValue,
        id: { value: experimentRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ExperimentFormDefaults {
    return {
      id: null,
      involvedStrains: [],
      precedingExperiments: [],
      followupExperiments: [],
    };
  }
}
