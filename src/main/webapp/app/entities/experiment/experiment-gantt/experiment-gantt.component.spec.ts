import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExperimentGanttComponent } from './experiment-gantt.component';

describe('ExperimentGanttComponent', () => {
  let component: ExperimentGanttComponent;
  let fixture: ComponentFixture<ExperimentGanttComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ExperimentGanttComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ExperimentGanttComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
