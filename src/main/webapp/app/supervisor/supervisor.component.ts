import { Component, OnInit } from '@angular/core';
import { ReporteService } from '../entities/reporte/service/reporte.service';

@Component({
  selector: 'jhi-supervisor',
  templateUrl: './supervisor.component.html',
  styleUrls: ['./supervisor.component.scss'],
})
export class SupervisorComponent implements OnInit {
  data: any;

  options: any;

  constructor(protected reporteService: ReporteService) {}

  ngOnInit(): void {
    this.data = {
      labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July'],
      datasets: [
        {
          label: 'First Dataset',
          data: [65, 59, 80, 81, 56, 55, 40],
        },
        {
          label: 'Second Dataset',
          data: [28, 48, 40, 19, 86, 27, 90],
        },
      ],
    };

    this.options = {
      animations: {
        tension: {
          duration: 1000,
          easing: 'linear',
          from: 1,
          to: 0,
          loop: true,
        },
      },
      scales: {
        y: {
          // defining min and max so hiding the dataset does not change scale range
          min: 0,
          max: 100,
        },
      },
      title: {
        display: true,
        text: 'My Title',
        fontSize: 16,
      },
      legend: {
        display: true,
        position: 'bottom',
      },
    };
  }
}
