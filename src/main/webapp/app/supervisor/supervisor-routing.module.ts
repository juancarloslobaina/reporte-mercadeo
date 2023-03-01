import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from '../login/login.component';
import { SupervisorComponent } from './supervisor.component';

const routes: Routes = [
  {
    path: '',
    component: SupervisorComponent,
  },
  {
    path: 'informes',
    data: { pageTitle: 'reportemercadeoApp.supervisor.home.title' },
    loadChildren: () => import('./supervisor.module').then(m => m.SupervisorModule),
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class SupervisorRoutingModule {}
