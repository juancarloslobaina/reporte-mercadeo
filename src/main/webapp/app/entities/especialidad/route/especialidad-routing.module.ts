import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { EspecialidadComponent } from '../list/especialidad.component';
import { EspecialidadDetailComponent } from '../detail/especialidad-detail.component';
import { EspecialidadUpdateComponent } from '../update/especialidad-update.component';
import { EspecialidadRoutingResolveService } from './especialidad-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const especialidadRoute: Routes = [
  {
    path: '',
    component: EspecialidadComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: EspecialidadDetailComponent,
    resolve: {
      especialidad: EspecialidadRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: EspecialidadUpdateComponent,
    resolve: {
      especialidad: EspecialidadRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: EspecialidadUpdateComponent,
    resolve: {
      especialidad: EspecialidadRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(especialidadRoute)],
  exports: [RouterModule],
})
export class EspecialidadRoutingModule {}
