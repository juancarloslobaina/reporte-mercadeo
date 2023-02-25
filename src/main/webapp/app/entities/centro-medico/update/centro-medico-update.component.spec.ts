import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { CentroMedicoFormService } from './centro-medico-form.service';
import { CentroMedicoService } from '../service/centro-medico.service';
import { ICentroMedico } from '../centro-medico.model';
import { ICiudad } from 'app/entities/ciudad/ciudad.model';
import { CiudadService } from 'app/entities/ciudad/service/ciudad.service';

import { CentroMedicoUpdateComponent } from './centro-medico-update.component';

describe('CentroMedico Management Update Component', () => {
  let comp: CentroMedicoUpdateComponent;
  let fixture: ComponentFixture<CentroMedicoUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let centroMedicoFormService: CentroMedicoFormService;
  let centroMedicoService: CentroMedicoService;
  let ciudadService: CiudadService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [CentroMedicoUpdateComponent],
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
      .overrideTemplate(CentroMedicoUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CentroMedicoUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    centroMedicoFormService = TestBed.inject(CentroMedicoFormService);
    centroMedicoService = TestBed.inject(CentroMedicoService);
    ciudadService = TestBed.inject(CiudadService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Ciudad query and add missing value', () => {
      const centroMedico: ICentroMedico = { id: 456 };
      const ciudad: ICiudad = { id: 11168 };
      centroMedico.ciudad = ciudad;

      const ciudadCollection: ICiudad[] = [{ id: 85173 }];
      jest.spyOn(ciudadService, 'query').mockReturnValue(of(new HttpResponse({ body: ciudadCollection })));
      const additionalCiudads = [ciudad];
      const expectedCollection: ICiudad[] = [...additionalCiudads, ...ciudadCollection];
      jest.spyOn(ciudadService, 'addCiudadToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ centroMedico });
      comp.ngOnInit();

      expect(ciudadService.query).toHaveBeenCalled();
      expect(ciudadService.addCiudadToCollectionIfMissing).toHaveBeenCalledWith(
        ciudadCollection,
        ...additionalCiudads.map(expect.objectContaining)
      );
      expect(comp.ciudadsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const centroMedico: ICentroMedico = { id: 456 };
      const ciudad: ICiudad = { id: 24655 };
      centroMedico.ciudad = ciudad;

      activatedRoute.data = of({ centroMedico });
      comp.ngOnInit();

      expect(comp.ciudadsSharedCollection).toContain(ciudad);
      expect(comp.centroMedico).toEqual(centroMedico);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICentroMedico>>();
      const centroMedico = { id: 123 };
      jest.spyOn(centroMedicoFormService, 'getCentroMedico').mockReturnValue(centroMedico);
      jest.spyOn(centroMedicoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ centroMedico });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: centroMedico }));
      saveSubject.complete();

      // THEN
      expect(centroMedicoFormService.getCentroMedico).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(centroMedicoService.update).toHaveBeenCalledWith(expect.objectContaining(centroMedico));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICentroMedico>>();
      const centroMedico = { id: 123 };
      jest.spyOn(centroMedicoFormService, 'getCentroMedico').mockReturnValue({ id: null });
      jest.spyOn(centroMedicoService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ centroMedico: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: centroMedico }));
      saveSubject.complete();

      // THEN
      expect(centroMedicoFormService.getCentroMedico).toHaveBeenCalled();
      expect(centroMedicoService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICentroMedico>>();
      const centroMedico = { id: 123 };
      jest.spyOn(centroMedicoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ centroMedico });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(centroMedicoService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareCiudad', () => {
      it('Should forward to ciudadService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(ciudadService, 'compareCiudad');
        comp.compareCiudad(entity, entity2);
        expect(ciudadService.compareCiudad).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
