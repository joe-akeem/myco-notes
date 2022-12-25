import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IStrain, NewStrain } from '../strain.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IStrain for edit and NewStrainFormGroupInput for create.
 */
type StrainFormGroupInput = IStrain | PartialWithRequiredKeyOf<NewStrain>;

type StrainFormDefaults = Pick<NewStrain, 'id' | 'fruiting' | 'experiments'>;

type StrainFormGroupContent = {
  id: FormControl<IStrain['id'] | NewStrain['id']>;
  name: FormControl<IStrain['name']>;
  description: FormControl<IStrain['description']>;
  isolatedAt: FormControl<IStrain['isolatedAt']>;
  fruiting: FormControl<IStrain['fruiting']>;
  species: FormControl<IStrain['species']>;
  origin: FormControl<IStrain['origin']>;
  experiments: FormControl<IStrain['experiments']>;
};

export type StrainFormGroup = FormGroup<StrainFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class StrainFormService {
  createStrainFormGroup(strain: StrainFormGroupInput = { id: null }): StrainFormGroup {
    const strainRawValue = {
      ...this.getFormDefaults(),
      ...strain,
    };
    return new FormGroup<StrainFormGroupContent>({
      id: new FormControl(
        { value: strainRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      name: new FormControl(strainRawValue.name, {
        validators: [Validators.required],
      }),
      description: new FormControl(strainRawValue.description),
      isolatedAt: new FormControl(strainRawValue.isolatedAt, {
        validators: [Validators.required],
      }),
      fruiting: new FormControl(strainRawValue.fruiting),
      species: new FormControl(strainRawValue.species, {
        validators: [Validators.required],
      }),
      origin: new FormControl(strainRawValue.origin, {
        validators: [Validators.required],
      }),
      experiments: new FormControl(strainRawValue.experiments ?? []),
    });
  }

  getStrain(form: StrainFormGroup): IStrain | NewStrain {
    return form.getRawValue() as IStrain | NewStrain;
  }

  resetForm(form: StrainFormGroup, strain: StrainFormGroupInput): void {
    const strainRawValue = { ...this.getFormDefaults(), ...strain };
    form.reset(
      {
        ...strainRawValue,
        id: { value: strainRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): StrainFormDefaults {
    return {
      id: null,
      fruiting: false,
      experiments: [],
    };
  }
}
