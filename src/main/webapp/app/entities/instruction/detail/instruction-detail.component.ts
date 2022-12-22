import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IInstruction } from '../instruction.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-instruction-detail',
  templateUrl: './instruction-detail.component.html',
})
export class InstructionDetailComponent implements OnInit {
  instruction: IInstruction | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ instruction }) => {
      this.instruction = instruction;
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  previousState(): void {
    window.history.back();
  }
}
