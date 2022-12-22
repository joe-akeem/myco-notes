import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ITek, NewTek } from '../tek.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITek for edit and NewTekFormGroupInput for create.
 */
type TekFormGroupInput = ITek | PartialWithRequiredKeyOf<NewTek>;

type TekFormDefaults = Pick<NewTek, 'id'>;

type TekFormGroupContent = {
  id: FormControl<ITek['id'] | NewTek['id']>;
  title: FormControl<ITek['title']>;
  description: FormControl<ITek['description']>;
};

export type TekFormGroup = FormGroup<TekFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TekFormService {
  createTekFormGroup(tek: TekFormGroupInput = { id: null }): TekFormGroup {
    const tekRawValue = {
      ...this.getFormDefaults(),
      ...tek,
    };
    return new FormGroup<TekFormGroupContent>({
      id: new FormControl(
        { value: tekRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      title: new FormControl(tekRawValue.title, {
        validators: [Validators.required],
      }),
      description: new FormControl(tekRawValue.description),
    });
  }

  getTek(form: TekFormGroup): ITek | NewTek {
    return form.getRawValue() as ITek | NewTek;
  }

  resetForm(form: TekFormGroup, tek: TekFormGroupInput): void {
    const tekRawValue = { ...this.getFormDefaults(), ...tek };
    form.reset(
      {
        ...tekRawValue,
        id: { value: tekRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): TekFormDefaults {
    return {
      id: null,
    };
  }
}
