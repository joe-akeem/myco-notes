import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DependencyChartComponent } from './dependency-chart.component';

describe('DependencyChartComponent', () => {
  let component: DependencyChartComponent;
  let fixture: ComponentFixture<DependencyChartComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DependencyChartComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(DependencyChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
