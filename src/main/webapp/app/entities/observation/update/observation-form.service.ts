import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IObservation, NewObservation } from '../observation.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IObservation for edit and NewObservationFormGroupInput for create.
 */
type ObservationFormGroupInput = IObservation | PartialWithRequiredKeyOf<NewObservation>;

type ObservationFormDefaults = Pick<NewObservation, 'id'>;

type ObservationFormGroupContent = {
  id: FormControl<IObservation['id'] | NewObservation['id']>;
  observationDate: FormControl<IObservation['observationDate']>;
  title: FormControl<IObservation['title']>;
  description: FormControl<IObservation['description']>;
  experiment: FormControl<IObservation['experiment']>;
};

export type ObservationFormGroup = FormGroup<ObservationFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ObservationFormService {
  createObservationFormGroup(observation: ObservationFormGroupInput = { id: null }): ObservationFormGroup {
    const observationRawValue = {
      ...this.getFormDefaults(),
      ...observation,
    };
    return new FormGroup<ObservationFormGroupContent>({
      id: new FormControl(
        { value: observationRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      observationDate: new FormControl(observationRawValue.observationDate, {
        validators: [Validators.required],
      }),
      title: new FormControl(observationRawValue.title, {
        validators: [Validators.required],
      }),
      description: new FormControl(observationRawValue.description),
      experiment: new FormControl(observationRawValue.experiment, {
        validators: [Validators.required],
      }),
    });
  }

  getObservation(form: ObservationFormGroup): IObservation | NewObservation {
    return form.getRawValue() as IObservation | NewObservation;
  }

  resetForm(form: ObservationFormGroup, observation: ObservationFormGroupInput): void {
    const observationRawValue = { ...this.getFormDefaults(), ...observation };
    form.reset(
      {
        ...observationRawValue,
        id: { value: observationRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ObservationFormDefaults {
    return {
      id: null,
    };
  }
}
