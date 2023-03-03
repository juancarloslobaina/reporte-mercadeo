import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { CiudadFormService } from './ciudad-form.service';
import { CiudadService } from '../service/ciudad.service';
import { ICiudad } from '../ciudad.model';

import { CiudadUpdateComponent } from './ciudad-update.component';

describe('Ciudad Management Update Component', () => {
  let comp: CiudadUpdateComponent;
  let fixture: ComponentFixture<CiudadUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let ciudadFormService: CiudadFormService;
  let ciudadService: CiudadService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [CiudadUpdateComponent],
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
      .overrideTemplate(CiudadUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CiudadUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    ciudadFormService = TestBed.inject(CiudadFormService);
    ciudadService = TestBed.inject(CiudadService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const ciudad: ICiudad = { id: 456 };

      activatedRoute.data = of({ ciudad });
      comp.ngOnInit();

      expect(comp.ciudad).toEqual(ciudad);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICiudad>>();
      const ciudad = { id: 123 };
      jest.spyOn(ciudadFormService, 'getCiudad').mockReturnValue(ciudad);
      jest.spyOn(ciudadService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ciudad });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: ciudad }));
      saveSubject.complete();

      // THEN
      expect(ciudadFormService.getCiudad).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(ciudadService.update).toHaveBeenCalledWith(expect.objectContaining(ciudad));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICiudad>>();
      const ciudad = { id: 123 };
      jest.spyOn(ciudadFormService, 'getCiudad').mockReturnValue({ id: null });
      jest.spyOn(ciudadService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ciudad: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: ciudad }));
      saveSubject.complete();

      // THEN
      expect(ciudadFormService.getCiudad).toHaveBeenCalled();
      expect(ciudadService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICiudad>>();
      const ciudad = { id: 123 };
      jest.spyOn(ciudadService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ciudad });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(ciudadService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
