import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExperimentSankeyComponent } from './experiment-sankey.component';

describe('ExperimentSankeyComponent', () => {
  let component: ExperimentSankeyComponent;
  let fixture: ComponentFixture<ExperimentSankeyComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ExperimentSankeyComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ExperimentSankeyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
