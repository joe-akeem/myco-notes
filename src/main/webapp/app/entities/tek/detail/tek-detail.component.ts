import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITek } from '../tek.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-tek-detail',
  templateUrl: './tek-detail.component.html',
})
export class TekDetailComponent implements OnInit {
  tek: ITek | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ tek }) => {
      this.tek = tek;
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
