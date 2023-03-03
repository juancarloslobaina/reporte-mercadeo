import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../centro.test-samples';

import { CentroFormService } from './centro-form.service';

describe('Centro Form Service', () => {
  let service: CentroFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CentroFormService);
  });

  describe('Service methods', () => {
    describe('createCentroFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCentroFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nombre: expect.any(Object),
            ciudad: expect.any(Object),
            user: expect.any(Object),
          })
        );
      });

      it('passing ICentro should create a new form with FormGroup', () => {
        const formGroup = service.createCentroFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nombre: expect.any(Object),
            ciudad: expect.any(Object),
            user: expect.any(Object),
          })
        );
      });
    });

    describe('getCentro', () => {
      it('should return NewCentro for default Centro initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createCentroFormGroup(sampleWithNewData);

        const centro = service.getCentro(formGroup) as any;

        expect(centro).toMatchObject(sampleWithNewData);
      });

      it('should return NewCentro for empty Centro initial value', () => {
        const formGroup = service.createCentroFormGroup();

        const centro = service.getCentro(formGroup) as any;

        expect(centro).toMatchObject({});
      });

      it('should return ICentro', () => {
        const formGroup = service.createCentroFormGroup(sampleWithRequiredData);

        const centro = service.getCentro(formGroup) as any;

        expect(centro).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICentro should not enable id FormControl', () => {
        const formGroup = service.createCentroFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCentro should disable id FormControl', () => {
        const formGroup = service.createCentroFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
