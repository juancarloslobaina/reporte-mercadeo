import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { CentroMedicoComponent } from './list/centro-medico.component';
import { CentroMedicoDetailComponent } from './detail/centro-medico-detail.component';
import { CentroMedicoUpdateComponent } from './update/centro-medico-update.component';
import { CentroMedicoDeleteDialogComponent } from './delete/centro-medico-delete-dialog.component';
import { CentroMedicoRoutingModule } from './route/centro-medico-routing.module';

@NgModule({
  imports: [SharedModule, CentroMedicoRoutingModule],
  declarations: [CentroMedicoComponent, CentroMedicoDetailComponent, CentroMedicoUpdateComponent, CentroMedicoDeleteDialogComponent],
})
export class CentroMedicoModule {}
