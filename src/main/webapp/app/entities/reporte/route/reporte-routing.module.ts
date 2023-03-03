import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ReporteComponent } from '../list/reporte.component';
import { ReporteDetailComponent } from '../detail/reporte-detail.component';
import { ReporteUpdateComponent } from '../update/reporte-update.component';
import { ReporteRoutingResolveService } from './reporte-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const reporteRoute: Routes = [
  {
    path: '',
    component: ReporteComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ReporteDetailComponent,
    resolve: {
      reporte: ReporteRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ReporteUpdateComponent,
    resolve: {
      reporte: ReporteRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ReporteUpdateComponent,
    resolve: {
      reporte: ReporteRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(reporteRoute)],
  exports: [RouterModule],
})
export class ReporteRoutingModule {}
