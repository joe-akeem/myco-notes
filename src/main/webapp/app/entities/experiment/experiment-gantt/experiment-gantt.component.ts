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

  type: ChartType = ChartType.Sankey;

  chartColumns: Column[] = [
    { type: 'string', role: 'From' },
    { type: 'string', role: 'To' },
    { type: 'number', role: 'Weight' },
    { type: 'string', role: 'tooltip', properties: { html: true } },
  ];

  chartData: Row[] = [];

  height: number = 600;

  options = {
    tooltip: {
      isHtml: true,
    },
  };

  constructor(protected experimentService: ExperimentService) {}

  ngOnInit(): void {
    this.experimentService.getSankeyCartData(this.experimentId).subscribe(ganttData => {
      ganttData.body?.forEach(row => {
        this.chartData.push([row.from, row.to, row.weight, row.tooltip]);
      });
    });
  }
}
