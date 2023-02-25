import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { EspecialidadFormService } from './especialidad-form.service';
import { EspecialidadService } from '../service/especialidad.service';
import { IEspecialidad } from '../especialidad.model';

import { EspecialidadUpdateComponent } from './especialidad-update.component';

describe('Especialidad Management Update Component', () => {
  let comp: EspecialidadUpdateComponent;
  let fixture: ComponentFixture<EspecialidadUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let especialidadFormService: EspecialidadFormService;
  let especialidadService: EspecialidadService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [EspecialidadUpdateComponent],
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
      .overrideTemplate(EspecialidadUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(EspecialidadUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    especialidadFormService = TestBed.inject(EspecialidadFormService);
    especialidadService = TestBed.inject(EspecialidadService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const especialidad: IEspecialidad = { id: 456 };

      activatedRoute.data = of({ especialidad });
      comp.ngOnInit();

      expect(comp.especialidad).toEqual(especialidad);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEspecialidad>>();
      const especialidad = { id: 123 };
      jest.spyOn(especialidadFormService, 'getEspecialidad').mockReturnValue(especialidad);
      jest.spyOn(especialidadService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ especialidad });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: especialidad }));
      saveSubject.complete();

      // THEN
      expect(especialidadFormService.getEspecialidad).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(especialidadService.update).toHaveBeenCalledWith(expect.objectContaining(especialidad));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEspecialidad>>();
      const especialidad = { id: 123 };
      jest.spyOn(especialidadFormService, 'getEspecialidad').mockReturnValue({ id: null });
      jest.spyOn(especialidadService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ especialidad: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: especialidad }));
      saveSubject.complete();

      // THEN
      expect(especialidadFormService.getEspecialidad).toHaveBeenCalled();
      expect(especialidadService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEspecialidad>>();
      const especialidad = { id: 123 };
      jest.spyOn(especialidadService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ especialidad });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(especialidadService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
