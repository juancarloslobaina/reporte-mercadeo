import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { EspecialidadComponent } from './list/especialidad.component';
import { EspecialidadDetailComponent } from './detail/especialidad-detail.component';
import { EspecialidadUpdateComponent } from './update/especialidad-update.component';
import { EspecialidadDeleteDialogComponent } from './delete/especialidad-delete-dialog.component';
import { EspecialidadRoutingModule } from './route/especialidad-routing.module';

@NgModule({
  imports: [SharedModule, EspecialidadRoutingModule],
  declarations: [EspecialidadComponent, EspecialidadDetailComponent, EspecialidadUpdateComponent, EspecialidadDeleteDialogComponent],
})
export class EspecialidadModule {}
