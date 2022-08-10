import { NgModule, CUSTOM_ELEMENTS_SCHEMA  } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ReporteComponent } from './list/reporte.component';
import { ReporteDetailComponent } from './detail/reporte-detail.component';
import { ReporteUpdateComponent } from './update/reporte-update.component';
import { ReporteDeleteDialogComponent } from './delete/reporte-delete-dialog.component';
import { ReporteRoutingModule } from './route/reporte-routing.module';
import {AutoCompleteModule} from 'primeng/autocomplete';
@NgModule({
  imports: [SharedModule, ReporteRoutingModule, AutoCompleteModule],
  declarations: [ReporteComponent, ReporteDetailComponent, ReporteUpdateComponent, ReporteDeleteDialogComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class ReporteModule {}
