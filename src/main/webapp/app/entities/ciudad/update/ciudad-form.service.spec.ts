import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../ciudad.test-samples';

import { CiudadFormService } from './ciudad-form.service';

describe('Ciudad Form Service', () => {
  let service: CiudadFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CiudadFormService);
  });

  describe('Service methods', () => {
    describe('createCiudadFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCiudadFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nombreCiudad: expect.any(Object),
          })
        );
      });

      it('passing ICiudad should create a new form with FormGroup', () => {
        const formGroup = service.createCiudadFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nombreCiudad: expect.any(Object),
          })
        );
      });
    });

    describe('getCiudad', () => {
      it('should return NewCiudad for default Ciudad initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createCiudadFormGroup(sampleWithNewData);

        const ciudad = service.getCiudad(formGroup) as any;

        expect(ciudad).toMatchObject(sampleWithNewData);
      });

      it('should return NewCiudad for empty Ciudad initial value', () => {
        const formGroup = service.createCiudadFormGroup();

        const ciudad = service.getCiudad(formGroup) as any;

        expect(ciudad).toMatchObject({});
      });

      it('should return ICiudad', () => {
        const formGroup = service.createCiudadFormGroup(sampleWithRequiredData);

        const ciudad = service.getCiudad(formGroup) as any;

        expect(ciudad).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICiudad should not enable id FormControl', () => {
        const formGroup = service.createCiudadFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCiudad should disable id FormControl', () => {
        const formGroup = service.createCiudadFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
