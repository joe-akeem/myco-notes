import { Component, Input, OnInit } from '@angular/core';
import { ChartType, Column, Row } from 'angular-google-charts';
import { ExperimentService } from '../service/experiment.service';

@Component({
  selector: 'jhi-experiment-gantt',
  templateUrl: './experiment-gantt.component.html',
  styleUrls: ['./experiment-gantt.component.scss'],
})
export class ExperimentGanttComponent implements OnInit {
  @Input() experimentId: number = 0;

  type: ChartType = ChartType.Gantt;
  chartColumns: Column[] = [
    { type: 'string', role: 'Task ID' },
    { type: 'string', role: 'Task Name' },
    { type: 'string', role: 'Resource' },
    { type: 'date', role: 'Start Date' },
    { type: 'date', role: 'End Date' },
    { type: 'number', role: 'Duration' },
    { type: 'number', role: 'Percent Complete' },
    { type: 'string', role: 'Dependencies' },
  ];
  chartData: Row[] = [];
  height: number = 600;

  constructor(protected experimentService: ExperimentService) {}

  ngOnInit(): void {
    this.experimentService.getGanttData(this.experimentId).subscribe(ganttData => {
      ganttData.body?.forEach(row => {
        this.chartData.push([
          row.taskId,
          row.taskName,
          row.resource,
          row.startDate ? new Date(row.startDate) : null,
          row.endDate ? new Date(row.endDate) : null,
          row.duration,
          row.percentComplete,
          row.dependencies,
        ]);
      });
    });
  }
}
