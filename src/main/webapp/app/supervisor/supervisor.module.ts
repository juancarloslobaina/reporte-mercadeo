import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SupervisorComponent } from './supervisor.component';
import { SupervisorRoutingModule } from './supervisor-routing.module';
import { ChartModule } from 'primeng/chart';

@NgModule({
  declarations: [SupervisorComponent],
  imports: [CommonModule, SupervisorRoutingModule, ChartModule],
})
export class SupervisorModule {}
