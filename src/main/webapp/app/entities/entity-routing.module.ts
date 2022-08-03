import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'ciudad',
        data: { pageTitle: 'reporteMercadeoApp.ciudad.home.title' },
        loadChildren: () => import('./ciudad/ciudad.module').then(m => m.CiudadModule),
      },
      {
        path: 'especialidad',
        data: { pageTitle: 'reporteMercadeoApp.especialidad.home.title' },
        loadChildren: () => import('./especialidad/especialidad.module').then(m => m.EspecialidadModule),
      },
      {
        path: 'centro-medico',
        data: { pageTitle: 'reporteMercadeoApp.centroMedico.home.title' },
        loadChildren: () => import('./centro-medico/centro-medico.module').then(m => m.CentroMedicoModule),
      },
      {
        path: 'doctor',
        data: { pageTitle: 'reporteMercadeoApp.doctor.home.title' },
        loadChildren: () => import('./doctor/doctor.module').then(m => m.DoctorModule),
      },
      {
        path: 'reporte',
        data: { pageTitle: 'reporteMercadeoApp.reporte.home.title' },
        loadChildren: () => import('./reporte/reporte.module').then(m => m.ReporteModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
