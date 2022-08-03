import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { DoctorFormService } from './doctor-form.service';
import { DoctorService } from '../service/doctor.service';
import { IDoctor } from '../doctor.model';
import { IEspecialidad } from 'app/entities/especialidad/especialidad.model';
import { EspecialidadService } from 'app/entities/especialidad/service/especialidad.service';

import { DoctorUpdateComponent } from './doctor-update.component';

describe('Doctor Management Update Component', () => {
  let comp: DoctorUpdateComponent;
  let fixture: ComponentFixture<DoctorUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let doctorFormService: DoctorFormService;
  let doctorService: DoctorService;
  let especialidadService: EspecialidadService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [DoctorUpdateComponent],
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
      .overrideTemplate(DoctorUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DoctorUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    doctorFormService = TestBed.inject(DoctorFormService);
    doctorService = TestBed.inject(DoctorService);
    especialidadService = TestBed.inject(EspecialidadService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Especialidad query and add missing value', () => {
      const doctor: IDoctor = { id: 456 };
      const especialidad: IEspecialidad = { id: 51948 };
      doctor.especialidad = especialidad;

      const especialidadCollection: IEspecialidad[] = [{ id: 12605 }];
      jest.spyOn(especialidadService, 'query').mockReturnValue(of(new HttpResponse({ body: especialidadCollection })));
      const additionalEspecialidads = [especialidad];
      const expectedCollection: IEspecialidad[] = [...additionalEspecialidads, ...especialidadCollection];
      jest.spyOn(especialidadService, 'addEspecialidadToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ doctor });
      comp.ngOnInit();

      expect(especialidadService.query).toHaveBeenCalled();
      expect(especialidadService.addEspecialidadToCollectionIfMissing).toHaveBeenCalledWith(
        especialidadCollection,
        ...additionalEspecialidads.map(expect.objectContaining)
      );
      expect(comp.especialidadsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const doctor: IDoctor = { id: 456 };
      const especialidad: IEspecialidad = { id: 78759 };
      doctor.especialidad = especialidad;

      activatedRoute.data = of({ doctor });
      comp.ngOnInit();

      expect(comp.especialidadsSharedCollection).toContain(especialidad);
      expect(comp.doctor).toEqual(doctor);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDoctor>>();
      const doctor = { id: 123 };
      jest.spyOn(doctorFormService, 'getDoctor').mockReturnValue(doctor);
      jest.spyOn(doctorService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ doctor });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: doctor }));
      saveSubject.complete();

      // THEN
      expect(doctorFormService.getDoctor).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(doctorService.update).toHaveBeenCalledWith(expect.objectContaining(doctor));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDoctor>>();
      const doctor = { id: 123 };
      jest.spyOn(doctorFormService, 'getDoctor').mockReturnValue({ id: null });
      jest.spyOn(doctorService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ doctor: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: doctor }));
      saveSubject.complete();

      // THEN
      expect(doctorFormService.getDoctor).toHaveBeenCalled();
      expect(doctorService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDoctor>>();
      const doctor = { id: 123 };
      jest.spyOn(doctorService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ doctor });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(doctorService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareEspecialidad', () => {
      it('Should forward to especialidadService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(especialidadService, 'compareEspecialidad');
        comp.compareEspecialidad(entity, entity2);
        expect(especialidadService.compareEspecialidad).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
