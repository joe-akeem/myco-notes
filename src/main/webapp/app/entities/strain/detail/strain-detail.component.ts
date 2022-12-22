import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IStrain } from '../strain.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-strain-detail',
  templateUrl: './strain-detail.component.html',
})
export class StrainDetailComponent implements OnInit {
  strain: IStrain | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ strain }) => {
      this.strain = strain;
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
