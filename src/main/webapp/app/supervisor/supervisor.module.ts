import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from 'app/shared/shared.module';

import { SupervisorRoutingModule } from './supervisor-routing.module';
import { DashboardComponent } from './dashboard/dashboard.component';
import { ChartModule } from 'primeng/chart';

@NgModule({
  imports: [CommonModule, SupervisorRoutingModule, ChartModule, SharedModule],
  declarations: [DashboardComponent],
})
export class SupervisorModule {}
