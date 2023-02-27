import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ICiudad } from '../ciudad.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../ciudad.test-samples';

import { CiudadService } from './ciudad.service';

const requireRestSample: ICiudad = {
  ...sampleWithRequiredData,
};

describe('Ciudad Service', () => {
  let service: CiudadService;
  let httpMock: HttpTestingController;
  let expectedResult: ICiudad | ICiudad[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(CiudadService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Ciudad', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const ciudad = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(ciudad).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Ciudad', () => {
      const ciudad = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(ciudad).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Ciudad', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Ciudad', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Ciudad', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addCiudadToCollectionIfMissing', () => {
      it('should add a Ciudad to an empty array', () => {
        const ciudad: ICiudad = sampleWithRequiredData;
        expectedResult = service.addCiudadToCollectionIfMissing([], ciudad);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(ciudad);
      });

      it('should not add a Ciudad to an array that contains it', () => {
        const ciudad: ICiudad = sampleWithRequiredData;
        const ciudadCollection: ICiudad[] = [
          {
            ...ciudad,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCiudadToCollectionIfMissing(ciudadCollection, ciudad);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Ciudad to an array that doesn't contain it", () => {
        const ciudad: ICiudad = sampleWithRequiredData;
        const ciudadCollection: ICiudad[] = [sampleWithPartialData];
        expectedResult = service.addCiudadToCollectionIfMissing(ciudadCollection, ciudad);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(ciudad);
      });

      it('should add only unique Ciudad to an array', () => {
        const ciudadArray: ICiudad[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const ciudadCollection: ICiudad[] = [sampleWithRequiredData];
        expectedResult = service.addCiudadToCollectionIfMissing(ciudadCollection, ...ciudadArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const ciudad: ICiudad = sampleWithRequiredData;
        const ciudad2: ICiudad = sampleWithPartialData;
        expectedResult = service.addCiudadToCollectionIfMissing([], ciudad, ciudad2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(ciudad);
        expect(expectedResult).toContain(ciudad2);
      });

      it('should accept null and undefined values', () => {
        const ciudad: ICiudad = sampleWithRequiredData;
        expectedResult = service.addCiudadToCollectionIfMissing([], null, ciudad, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(ciudad);
      });

      it('should return initial array if no Ciudad is added', () => {
        const ciudadCollection: ICiudad[] = [sampleWithRequiredData];
        expectedResult = service.addCiudadToCollectionIfMissing(ciudadCollection, undefined, null);
        expect(expectedResult).toEqual(ciudadCollection);
      });
    });

    describe('compareCiudad', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCiudad(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareCiudad(entity1, entity2);
        const compareResult2 = service.compareCiudad(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareCiudad(entity1, entity2);
        const compareResult2 = service.compareCiudad(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareCiudad(entity1, entity2);
        const compareResult2 = service.compareCiudad(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
