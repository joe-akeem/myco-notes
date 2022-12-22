import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IInstruction, NewInstruction } from '../instruction.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IInstruction for edit and NewInstructionFormGroupInput for create.
 */
type InstructionFormGroupInput = IInstruction | PartialWithRequiredKeyOf<NewInstruction>;

type InstructionFormDefaults = Pick<NewInstruction, 'id'>;

type InstructionFormGroupContent = {
  id: FormControl<IInstruction['id'] | NewInstruction['id']>;
  title: FormControl<IInstruction['title']>;
  description: FormControl<IInstruction['description']>;
  instructionSet: FormControl<IInstruction['instructionSet']>;
};

export type InstructionFormGroup = FormGroup<InstructionFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class InstructionFormService {
  createInstructionFormGroup(instruction: InstructionFormGroupInput = { id: null }): InstructionFormGroup {
    const instructionRawValue = {
      ...this.getFormDefaults(),
      ...instruction,
    };
    return new FormGroup<InstructionFormGroupContent>({
      id: new FormControl(
        { value: instructionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      title: new FormControl(instructionRawValue.title, {
        validators: [Validators.required],
      }),
      description: new FormControl(instructionRawValue.description),
      instructionSet: new FormControl(instructionRawValue.instructionSet, {
        validators: [Validators.required],
      }),
    });
  }

  getInstruction(form: InstructionFormGroup): IInstruction | NewInstruction {
    return form.getRawValue() as IInstruction | NewInstruction;
  }

  resetForm(form: InstructionFormGroup, instruction: InstructionFormGroupInput): void {
    const instructionRawValue = { ...this.getFormDefaults(), ...instruction };
    form.reset(
      {
        ...instructionRawValue,
        id: { value: instructionRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): InstructionFormDefaults {
    return {
      id: null,
    };
  }
}
