import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { CentroComponent } from './list/centro.component';
import { CentroDetailComponent } from './detail/centro-detail.component';
import { CentroUpdateComponent } from './update/centro-update.component';
import { CentroDeleteDialogComponent } from './delete/centro-delete-dialog.component';
import { CentroRoutingModule } from './route/centro-routing.module';
import { AutoCompleteModule } from 'primeng/autocomplete';

@NgModule({
  imports: [SharedModule, CentroRoutingModule, AutoCompleteModule],
  declarations: [CentroComponent, CentroDetailComponent, CentroUpdateComponent, CentroDeleteDialogComponent],
})
export class CentroModule {}
