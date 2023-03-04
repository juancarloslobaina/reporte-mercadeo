import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { CentroComponent } from '../list/centro.component';
import { CentroDetailComponent } from '../detail/centro-detail.component';
import { CentroUpdateComponent } from '../update/centro-update.component';
import { CentroRoutingResolveService } from './centro-routing-resolve.service';
import { ASC, DESC } from 'app/config/navigation.constants';

const centroRoute: Routes = [
  {
    path: '',
    component: CentroComponent,
    data: {
      defaultSort: 'createdDate,' + DESC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CentroDetailComponent,
    resolve: {
      centro: CentroRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CentroUpdateComponent,
    resolve: {
      centro: CentroRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CentroUpdateComponent,
    resolve: {
      centro: CentroRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(centroRoute)],
  exports: [RouterModule],
})
export class CentroRoutingModule {}
