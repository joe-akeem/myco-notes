import { Component, Input, OnInit } from '@angular/core';
import { ChartType, Column, Row } from 'angular-google-charts';
import { ExperimentService } from '../service/experiment.service';

@Component({
  selector: 'jhi-experiment-sankey',
  templateUrl: './experiment-sankey.component.html',
  styleUrls: ['./experiment-sankey.component.scss'],
})
export class ExperimentSankeyComponent implements OnInit {
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
    this.experimentService.getSankeyChartData(this.experimentId).subscribe(data => {
      data.body?.forEach(row => {
        this.chartData.push([row.from, row.to, row.weight, row.tooltip]);
      });
    });
  }
}
