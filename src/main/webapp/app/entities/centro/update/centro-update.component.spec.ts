import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { CentroFormService } from './centro-form.service';
import { CentroService } from '../service/centro.service';
import { ICentro } from '../centro.model';
import { ICiudad } from 'app/entities/ciudad/ciudad.model';
import { CiudadService } from 'app/entities/ciudad/service/ciudad.service';

import { CentroUpdateComponent } from './centro-update.component';

describe('Centro Management Update Component', () => {
  let comp: CentroUpdateComponent;
  let fixture: ComponentFixture<CentroUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let centroFormService: CentroFormService;
  let centroService: CentroService;
  let ciudadService: CiudadService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [CentroUpdateComponent],
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
      .overrideTemplate(CentroUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CentroUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    centroFormService = TestBed.inject(CentroFormService);
    centroService = TestBed.inject(CentroService);
    ciudadService = TestBed.inject(CiudadService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Ciudad query and add missing value', () => {
      const centro: ICentro = { id: 456 };
      const ciudad: ICiudad = { id: 41932 };
      centro.ciudad = ciudad;

      const ciudadCollection: ICiudad[] = [{ id: 52731 }];
      jest.spyOn(ciudadService, 'query').mockReturnValue(of(new HttpResponse({ body: ciudadCollection })));
      const additionalCiudads = [ciudad];
      const expectedCollection: ICiudad[] = [...additionalCiudads, ...ciudadCollection];
      jest.spyOn(ciudadService, 'addCiudadToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ centro });
      comp.ngOnInit();

      expect(ciudadService.query).toHaveBeenCalled();
      expect(ciudadService.addCiudadToCollectionIfMissing).toHaveBeenCalledWith(
        ciudadCollection,
        ...additionalCiudads.map(expect.objectContaining)
      );
      expect(comp.ciudadsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const centro: ICentro = { id: 456 };
      const ciudad: ICiudad = { id: 15341 };
      centro.ciudad = ciudad;

      activatedRoute.data = of({ centro });
      comp.ngOnInit();

      expect(comp.ciudadsSharedCollection).toContain(ciudad);
      expect(comp.centro).toEqual(centro);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICentro>>();
      const centro = { id: 123 };
      jest.spyOn(centroFormService, 'getCentro').mockReturnValue(centro);
      jest.spyOn(centroService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ centro });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: centro }));
      saveSubject.complete();

      // THEN
      expect(centroFormService.getCentro).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(centroService.update).toHaveBeenCalledWith(expect.objectContaining(centro));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICentro>>();
      const centro = { id: 123 };
      jest.spyOn(centroFormService, 'getCentro').mockReturnValue({ id: null });
      jest.spyOn(centroService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ centro: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: centro }));
      saveSubject.complete();

      // THEN
      expect(centroFormService.getCentro).toHaveBeenCalled();
      expect(centroService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICentro>>();
      const centro = { id: 123 };
      jest.spyOn(centroService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ centro });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(centroService.update).toHaveBeenCalled();
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
