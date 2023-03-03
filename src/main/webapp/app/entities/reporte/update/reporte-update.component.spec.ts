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
import { ICentro } from 'app/entities/centro/centro.model';
import { CentroService } from 'app/entities/centro/service/centro.service';
import { IDoctor } from 'app/entities/doctor/doctor.model';
import { DoctorService } from 'app/entities/doctor/service/doctor.service';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { ReporteUpdateComponent } from './reporte-update.component';

describe('Reporte Management Update Component', () => {
  let comp: ReporteUpdateComponent;
  let fixture: ComponentFixture<ReporteUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let reporteFormService: ReporteFormService;
  let reporteService: ReporteService;
  let centroService: CentroService;
  let doctorService: DoctorService;
  let userService: UserService;

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
    centroService = TestBed.inject(CentroService);
    doctorService = TestBed.inject(DoctorService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Centro query and add missing value', () => {
      const reporte: IReporte = { id: 456 };
      const centro: ICentro = { id: 14468 };
      reporte.centro = centro;

      const centroCollection: ICentro[] = [{ id: 70236 }];
      jest.spyOn(centroService, 'query').mockReturnValue(of(new HttpResponse({ body: centroCollection })));
      const additionalCentros = [centro];
      const expectedCollection: ICentro[] = [...additionalCentros, ...centroCollection];
      jest.spyOn(centroService, 'addCentroToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ reporte });
      comp.ngOnInit();

      expect(centroService.query).toHaveBeenCalled();
      expect(centroService.addCentroToCollectionIfMissing).toHaveBeenCalledWith(
        centroCollection,
        ...additionalCentros.map(expect.objectContaining)
      );
      expect(comp.centrosSharedCollection).toEqual(expectedCollection);
    });

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

    it('Should call User query and add missing value', () => {
      const reporte: IReporte = { id: 456 };
      const user: IUser = { id: 91094 };
      reporte.user = user;

      const userCollection: IUser[] = [{ id: 41893 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ reporte });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const reporte: IReporte = { id: 456 };
      const centro: ICentro = { id: 67935 };
      reporte.centro = centro;
      const doctor: IDoctor = { id: 40199 };
      reporte.doctor = doctor;
      const user: IUser = { id: 86186 };
      reporte.user = user;

      activatedRoute.data = of({ reporte });
      comp.ngOnInit();

      expect(comp.centrosSharedCollection).toContain(centro);
      expect(comp.doctorsSharedCollection).toContain(doctor);
      expect(comp.usersSharedCollection).toContain(user);
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
    describe('compareCentro', () => {
      it('Should forward to centroService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(centroService, 'compareCentro');
        comp.compareCentro(entity, entity2);
        expect(centroService.compareCentro).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareDoctor', () => {
      it('Should forward to doctorService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(doctorService, 'compareDoctor');
        comp.compareDoctor(entity, entity2);
        expect(doctorService.compareDoctor).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareUser', () => {
      it('Should forward to userService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
