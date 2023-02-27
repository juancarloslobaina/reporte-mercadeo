import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'ciudad',
        data: { pageTitle: 'reportemercadeoApp.ciudad.home.title' },
        loadChildren: () => import('./ciudad/ciudad.module').then(m => m.CiudadModule),
      },
      {
        path: 'especialidad',
        data: { pageTitle: 'reportemercadeoApp.especialidad.home.title' },
        loadChildren: () => import('./especialidad/especialidad.module').then(m => m.EspecialidadModule),
      },
      {
        path: 'centro',
        data: { pageTitle: 'reportemercadeoApp.centro.home.title' },
        loadChildren: () => import('./centro/centro.module').then(m => m.CentroModule),
      },
      {
        path: 'doctor',
        data: { pageTitle: 'reportemercadeoApp.doctor.home.title' },
        loadChildren: () => import('./doctor/doctor.module').then(m => m.DoctorModule),
      },
      {
        path: 'reporte',
        data: { pageTitle: 'reportemercadeoApp.reporte.home.title' },
        loadChildren: () => import('./reporte/reporte.module').then(m => m.ReporteModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
