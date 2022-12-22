import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IGenus, NewGenus } from '../genus.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IGenus for edit and NewGenusFormGroupInput for create.
 */
type GenusFormGroupInput = IGenus | PartialWithRequiredKeyOf<NewGenus>;

type GenusFormDefaults = Pick<NewGenus, 'id'>;

type GenusFormGroupContent = {
  id: FormControl<IGenus['id'] | NewGenus['id']>;
  name: FormControl<IGenus['name']>;
  commonName: FormControl<IGenus['commonName']>;
  description: FormControl<IGenus['description']>;
};

export type GenusFormGroup = FormGroup<GenusFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class GenusFormService {
  createGenusFormGroup(genus: GenusFormGroupInput = { id: null }): GenusFormGroup {
    const genusRawValue = {
      ...this.getFormDefaults(),
      ...genus,
    };
    return new FormGroup<GenusFormGroupContent>({
      id: new FormControl(
        { value: genusRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      name: new FormControl(genusRawValue.name, {
        validators: [Validators.required],
      }),
      commonName: new FormControl(genusRawValue.commonName),
      description: new FormControl(genusRawValue.description),
    });
  }

  getGenus(form: GenusFormGroup): IGenus | NewGenus {
    return form.getRawValue() as IGenus | NewGenus;
  }

  resetForm(form: GenusFormGroup, genus: GenusFormGroupInput): void {
    const genusRawValue = { ...this.getFormDefaults(), ...genus };
    form.reset(
      {
        ...genusRawValue,
        id: { value: genusRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): GenusFormDefaults {
    return {
      id: null,
    };
  }
}
