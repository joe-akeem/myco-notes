import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ISpecies, NewSpecies } from '../species.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISpecies for edit and NewSpeciesFormGroupInput for create.
 */
type SpeciesFormGroupInput = ISpecies | PartialWithRequiredKeyOf<NewSpecies>;

type SpeciesFormDefaults = Pick<NewSpecies, 'id'>;

type SpeciesFormGroupContent = {
  id: FormControl<ISpecies['id'] | NewSpecies['id']>;
  name: FormControl<ISpecies['name']>;
  commonName: FormControl<ISpecies['commonName']>;
  description: FormControl<ISpecies['description']>;
  genus: FormControl<ISpecies['genus']>;
};

export type SpeciesFormGroup = FormGroup<SpeciesFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SpeciesFormService {
  createSpeciesFormGroup(species: SpeciesFormGroupInput = { id: null }): SpeciesFormGroup {
    const speciesRawValue = {
      ...this.getFormDefaults(),
      ...species,
    };
    return new FormGroup<SpeciesFormGroupContent>({
      id: new FormControl(
        { value: speciesRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      name: new FormControl(speciesRawValue.name, {
        validators: [Validators.required],
      }),
      commonName: new FormControl(speciesRawValue.commonName),
      description: new FormControl(speciesRawValue.description),
      genus: new FormControl(speciesRawValue.genus, {
        validators: [Validators.required],
      }),
    });
  }

  getSpecies(form: SpeciesFormGroup): ISpecies | NewSpecies {
    return form.getRawValue() as ISpecies | NewSpecies;
  }

  resetForm(form: SpeciesFormGroup, species: SpeciesFormGroupInput): void {
    const speciesRawValue = { ...this.getFormDefaults(), ...species };
    form.reset(
      {
        ...speciesRawValue,
        id: { value: speciesRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): SpeciesFormDefaults {
    return {
      id: null,
    };
  }
}
