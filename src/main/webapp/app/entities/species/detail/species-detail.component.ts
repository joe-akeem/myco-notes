import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ISpecies } from '../species.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-species-detail',
  templateUrl: './species-detail.component.html',
})
export class SpeciesDetailComponent implements OnInit {
  species: ISpecies | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ species }) => {
      this.species = species;
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
