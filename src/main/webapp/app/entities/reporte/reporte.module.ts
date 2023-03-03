import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ReporteComponent } from './list/reporte.component';
import { ReporteDetailComponent } from './detail/reporte-detail.component';
import { ReporteUpdateComponent } from './update/reporte-update.component';
import { ReporteDeleteDialogComponent } from './delete/reporte-delete-dialog.component';
import { ReporteRoutingModule } from './route/reporte-routing.module';

@NgModule({
  imports: [SharedModule, ReporteRoutingModule],
  declarations: [ReporteComponent, ReporteDetailComponent, ReporteUpdateComponent, ReporteDeleteDialogComponent],
})
export class ReporteModule {}
