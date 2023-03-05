import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ReporteComponent } from './list/reporte.component';
import { ReporteDetailComponent } from './detail/reporte-detail.component';
import { ReporteUpdateComponent } from './update/reporte-update.component';
import { ReporteDeleteDialogComponent } from './delete/reporte-delete-dialog.component';
import { ReporteRoutingModule } from './route/reporte-routing.module';
import { AutoCompleteModule } from 'primeng/autocomplete';
import { TableModule } from 'primeng/table';
import { DropdownModule } from 'primeng/dropdown';
import { SliderModule } from 'primeng/slider';
import { ProgressBarModule } from 'primeng/progressbar';
import { MultiSelectModule } from 'primeng/multiselect';

@NgModule({
  imports: [
    SharedModule,
    ReporteRoutingModule,
    AutoCompleteModule,
    TableModule,
    DropdownModule,
    SliderModule,
    ProgressBarModule,
    MultiSelectModule,
  ],
  declarations: [ReporteComponent, ReporteDetailComponent, ReporteUpdateComponent, ReporteDeleteDialogComponent],
})
export class ReporteModule {}
