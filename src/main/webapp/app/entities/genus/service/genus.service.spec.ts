import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IGenus } from '../genus.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../genus.test-samples';

import { GenusService } from './genus.service';

const requireRestSample: IGenus = {
  ...sampleWithRequiredData,
};

describe('Genus Service', () => {
  let service: GenusService;
  let httpMock: HttpTestingController;
  let expectedResult: IGenus | IGenus[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(GenusService);
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

    it('should create a Genus', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const genus = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(genus).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Genus', () => {
      const genus = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(genus).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Genus', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Genus', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Genus', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addGenusToCollectionIfMissing', () => {
      it('should add a Genus to an empty array', () => {
        const genus: IGenus = sampleWithRequiredData;
        expectedResult = service.addGenusToCollectionIfMissing([], genus);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(genus);
      });

      it('should not add a Genus to an array that contains it', () => {
        const genus: IGenus = sampleWithRequiredData;
        const genusCollection: IGenus[] = [
          {
            ...genus,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addGenusToCollectionIfMissing(genusCollection, genus);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Genus to an array that doesn't contain it", () => {
        const genus: IGenus = sampleWithRequiredData;
        const genusCollection: IGenus[] = [sampleWithPartialData];
        expectedResult = service.addGenusToCollectionIfMissing(genusCollection, genus);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(genus);
      });

      it('should add only unique Genus to an array', () => {
        const genusArray: IGenus[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const genusCollection: IGenus[] = [sampleWithRequiredData];
        expectedResult = service.addGenusToCollectionIfMissing(genusCollection, ...genusArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const genus: IGenus = sampleWithRequiredData;
        const genus2: IGenus = sampleWithPartialData;
        expectedResult = service.addGenusToCollectionIfMissing([], genus, genus2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(genus);
        expect(expectedResult).toContain(genus2);
      });

      it('should accept null and undefined values', () => {
        const genus: IGenus = sampleWithRequiredData;
        expectedResult = service.addGenusToCollectionIfMissing([], null, genus, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(genus);
      });

      it('should return initial array if no Genus is added', () => {
        const genusCollection: IGenus[] = [sampleWithRequiredData];
        expectedResult = service.addGenusToCollectionIfMissing(genusCollection, undefined, null);
        expect(expectedResult).toEqual(genusCollection);
      });
    });

    describe('compareGenus', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareGenus(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareGenus(entity1, entity2);
        const compareResult2 = service.compareGenus(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareGenus(entity1, entity2);
        const compareResult2 = service.compareGenus(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareGenus(entity1, entity2);
        const compareResult2 = service.compareGenus(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
