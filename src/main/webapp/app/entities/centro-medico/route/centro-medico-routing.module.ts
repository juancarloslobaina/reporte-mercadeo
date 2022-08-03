import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { CentroMedicoComponent } from '../list/centro-medico.component';
import { CentroMedicoDetailComponent } from '../detail/centro-medico-detail.component';
import { CentroMedicoUpdateComponent } from '../update/centro-medico-update.component';
import { CentroMedicoRoutingResolveService } from './centro-medico-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const centroMedicoRoute: Routes = [
  {
    path: '',
    component: CentroMedicoComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CentroMedicoDetailComponent,
    resolve: {
      centroMedico: CentroMedicoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CentroMedicoUpdateComponent,
    resolve: {
      centroMedico: CentroMedicoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CentroMedicoUpdateComponent,
    resolve: {
      centroMedico: CentroMedicoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(centroMedicoRoute)],
  exports: [RouterModule],
})
export class CentroMedicoRoutingModule {}
