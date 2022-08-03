import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ReporteFormService } from './reporte-form.service';
import { ReporteService } from '../service/reporte.service';
import { IReporte } from '../reporte.model';
import { IDoctor } from 'app/entities/doctor/doctor.model';
import { DoctorService } from 'app/entities/doctor/service/doctor.service';
import { ICentroMedico } from 'app/entities/centro-medico/centro-medico.model';
import { CentroMedicoService } from 'app/entities/centro-medico/service/centro-medico.service';

import { ReporteUpdateComponent } from './reporte-update.component';

describe('Reporte Management Update Component', () => {
  let comp: ReporteUpdateComponent;
  let fixture: ComponentFixture<ReporteUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let reporteFormService: ReporteFormService;
  let reporteService: ReporteService;
  let doctorService: DoctorService;
  let centroMedicoService: CentroMedicoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ReporteUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(ReporteUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ReporteUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    reporteFormService = TestBed.inject(ReporteFormService);
    reporteService = TestBed.inject(ReporteService);
    doctorService = TestBed.inject(DoctorService);
    centroMedicoService = TestBed.inject(CentroMedicoService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Doctor query and add missing value', () => {
      const reporte: IReporte = { id: 456 };
      const doctor: IDoctor = { id: 87930 };
      reporte.doctor = doctor;

      const doctorCollection: IDoctor[] = [{ id: 45429 }];
      jest.spyOn(doctorService, 'query').mockReturnValue(of(new HttpResponse({ body: doctorCollection })));
      const additionalDoctors = [doctor];
      const expectedCollection: IDoctor[] = [...additionalDoctors, ...doctorCollection];
      jest.spyOn(doctorService, 'addDoctorToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ reporte });
      comp.ngOnInit();

      expect(doctorService.query).toHaveBeenCalled();
      expect(doctorService.addDoctorToCollectionIfMissing).toHaveBeenCalledWith(
        doctorCollection,
        ...additionalDoctors.map(expect.objectContaining)
      );
      expect(comp.doctorsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call CentroMedico query and add missing value', () => {
      const reporte: IReporte = { id: 456 };
      const centroMedico: ICentroMedico = { id: 91560 };
      reporte.centroMedico = centroMedico;

      const centroMedicoCollection: ICentroMedico[] = [{ id: 30037 }];
      jest.spyOn(centroMedicoService, 'query').mockReturnValue(of(new HttpResponse({ body: centroMedicoCollection })));
      const additionalCentroMedicos = [centroMedico];
      const expectedCollection: ICentroMedico[] = [...additionalCentroMedicos, ...centroMedicoCollection];
      jest.spyOn(centroMedicoService, 'addCentroMedicoToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ reporte });
      comp.ngOnInit();

      expect(centroMedicoService.query).toHaveBeenCalled();
      expect(centroMedicoService.addCentroMedicoToCollectionIfMissing).toHaveBeenCalledWith(
        centroMedicoCollection,
        ...additionalCentroMedicos.map(expect.objectContaining)
      );
      expect(comp.centroMedicosSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const reporte: IReporte = { id: 456 };
      const doctor: IDoctor = { id: 40199 };
      reporte.doctor = doctor;
      const centroMedico: ICentroMedico = { id: 11775 };
      reporte.centroMedico = centroMedico;

      activatedRoute.data = of({ reporte });
      comp.ngOnInit();

      expect(comp.doctorsSharedCollection).toContain(doctor);
      expect(comp.centroMedicosSharedCollection).toContain(centroMedico);
      expect(comp.reporte).toEqual(reporte);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IReporte>>();
      const reporte = { id: 123 };
      jest.spyOn(reporteFormService, 'getReporte').mockReturnValue(reporte);
      jest.spyOn(reporteService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ reporte });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: reporte }));
      saveSubject.complete();

      // THEN
      expect(reporteFormService.getReporte).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(reporteService.update).toHaveBeenCalledWith(expect.objectContaining(reporte));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IReporte>>();
      const reporte = { id: 123 };
      jest.spyOn(reporteFormService, 'getReporte').mockReturnValue({ id: null });
      jest.spyOn(reporteService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ reporte: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: reporte }));
      saveSubject.complete();

      // THEN
      expect(reporteFormService.getReporte).toHaveBeenCalled();
      expect(reporteService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IReporte>>();
      const reporte = { id: 123 };
      jest.spyOn(reporteService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ reporte });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(reporteService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareDoctor', () => {
      it('Should forward to doctorService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(doctorService, 'compareDoctor');
        comp.compareDoctor(entity, entity2);
        expect(doctorService.compareDoctor).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareCentroMedico', () => {
      it('Should forward to centroMedicoService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(centroMedicoService, 'compareCentroMedico');
        comp.compareCentroMedico(entity, entity2);
        expect(centroMedicoService.compareCentroMedico).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
