import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'ciudad',
        data: { pageTitle: 'Ciudads' },
        loadChildren: () => import('./ciudad/ciudad.module').then(m => m.CiudadModule),
      },
      {
        path: 'especialidad',
        data: { pageTitle: 'Especialidads' },
        loadChildren: () => import('./especialidad/especialidad.module').then(m => m.EspecialidadModule),
      },
      {
        path: 'centro-medico',
        data: { pageTitle: 'CentroMedicos' },
        loadChildren: () => import('./centro-medico/centro-medico.module').then(m => m.CentroMedicoModule),
      },
      {
        path: 'doctor',
        data: { pageTitle: 'Doctors' },
        loadChildren: () => import('./doctor/doctor.module').then(m => m.DoctorModule),
      },
      {
        path: 'reporte',
        data: { pageTitle: 'Reportes' },
        loadChildren: () => import('./reporte/reporte.module').then(m => m.ReporteModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
