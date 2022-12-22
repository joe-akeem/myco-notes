import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ITek } from '../tek.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../tek.test-samples';

import { TekService } from './tek.service';

const requireRestSample: ITek = {
  ...sampleWithRequiredData,
};

describe('Tek Service', () => {
  let service: TekService;
  let httpMock: HttpTestingController;
  let expectedResult: ITek | ITek[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TekService);
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

    it('should create a Tek', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const tek = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(tek).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Tek', () => {
      const tek = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(tek).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Tek', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Tek', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Tek', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addTekToCollectionIfMissing', () => {
      it('should add a Tek to an empty array', () => {
        const tek: ITek = sampleWithRequiredData;
        expectedResult = service.addTekToCollectionIfMissing([], tek);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(tek);
      });

      it('should not add a Tek to an array that contains it', () => {
        const tek: ITek = sampleWithRequiredData;
        const tekCollection: ITek[] = [
          {
            ...tek,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTekToCollectionIfMissing(tekCollection, tek);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Tek to an array that doesn't contain it", () => {
        const tek: ITek = sampleWithRequiredData;
        const tekCollection: ITek[] = [sampleWithPartialData];
        expectedResult = service.addTekToCollectionIfMissing(tekCollection, tek);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(tek);
      });

      it('should add only unique Tek to an array', () => {
        const tekArray: ITek[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const tekCollection: ITek[] = [sampleWithRequiredData];
        expectedResult = service.addTekToCollectionIfMissing(tekCollection, ...tekArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const tek: ITek = sampleWithRequiredData;
        const tek2: ITek = sampleWithPartialData;
        expectedResult = service.addTekToCollectionIfMissing([], tek, tek2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(tek);
        expect(expectedResult).toContain(tek2);
      });

      it('should accept null and undefined values', () => {
        const tek: ITek = sampleWithRequiredData;
        expectedResult = service.addTekToCollectionIfMissing([], null, tek, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(tek);
      });

      it('should return initial array if no Tek is added', () => {
        const tekCollection: ITek[] = [sampleWithRequiredData];
        expectedResult = service.addTekToCollectionIfMissing(tekCollection, undefined, null);
        expect(expectedResult).toEqual(tekCollection);
      });
    });

    describe('compareTek', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTek(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareTek(entity1, entity2);
        const compareResult2 = service.compareTek(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareTek(entity1, entity2);
        const compareResult2 = service.compareTek(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareTek(entity1, entity2);
        const compareResult2 = service.compareTek(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
