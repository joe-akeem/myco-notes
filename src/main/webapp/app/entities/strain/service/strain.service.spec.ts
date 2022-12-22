import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IStrain } from '../strain.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../strain.test-samples';

import { StrainService, RestStrain } from './strain.service';

const requireRestSample: RestStrain = {
  ...sampleWithRequiredData,
  isolatedAt: sampleWithRequiredData.isolatedAt?.format(DATE_FORMAT),
};

describe('Strain Service', () => {
  let service: StrainService;
  let httpMock: HttpTestingController;
  let expectedResult: IStrain | IStrain[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(StrainService);
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

    it('should create a Strain', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const strain = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(strain).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Strain', () => {
      const strain = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(strain).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Strain', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Strain', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Strain', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addStrainToCollectionIfMissing', () => {
      it('should add a Strain to an empty array', () => {
        const strain: IStrain = sampleWithRequiredData;
        expectedResult = service.addStrainToCollectionIfMissing([], strain);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(strain);
      });

      it('should not add a Strain to an array that contains it', () => {
        const strain: IStrain = sampleWithRequiredData;
        const strainCollection: IStrain[] = [
          {
            ...strain,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addStrainToCollectionIfMissing(strainCollection, strain);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Strain to an array that doesn't contain it", () => {
        const strain: IStrain = sampleWithRequiredData;
        const strainCollection: IStrain[] = [sampleWithPartialData];
        expectedResult = service.addStrainToCollectionIfMissing(strainCollection, strain);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(strain);
      });

      it('should add only unique Strain to an array', () => {
        const strainArray: IStrain[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const strainCollection: IStrain[] = [sampleWithRequiredData];
        expectedResult = service.addStrainToCollectionIfMissing(strainCollection, ...strainArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const strain: IStrain = sampleWithRequiredData;
        const strain2: IStrain = sampleWithPartialData;
        expectedResult = service.addStrainToCollectionIfMissing([], strain, strain2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(strain);
        expect(expectedResult).toContain(strain2);
      });

      it('should accept null and undefined values', () => {
        const strain: IStrain = sampleWithRequiredData;
        expectedResult = service.addStrainToCollectionIfMissing([], null, strain, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(strain);
      });

      it('should return initial array if no Strain is added', () => {
        const strainCollection: IStrain[] = [sampleWithRequiredData];
        expectedResult = service.addStrainToCollectionIfMissing(strainCollection, undefined, null);
        expect(expectedResult).toEqual(strainCollection);
      });
    });

    describe('compareStrain', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareStrain(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareStrain(entity1, entity2);
        const compareResult2 = service.compareStrain(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareStrain(entity1, entity2);
        const compareResult2 = service.compareStrain(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareStrain(entity1, entity2);
        const compareResult2 = service.compareStrain(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
