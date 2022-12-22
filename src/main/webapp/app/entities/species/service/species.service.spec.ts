import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ISpecies } from '../species.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../species.test-samples';

import { SpeciesService } from './species.service';

const requireRestSample: ISpecies = {
  ...sampleWithRequiredData,
};

describe('Species Service', () => {
  let service: SpeciesService;
  let httpMock: HttpTestingController;
  let expectedResult: ISpecies | ISpecies[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(SpeciesService);
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

    it('should create a Species', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const species = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(species).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Species', () => {
      const species = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(species).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Species', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Species', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Species', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addSpeciesToCollectionIfMissing', () => {
      it('should add a Species to an empty array', () => {
        const species: ISpecies = sampleWithRequiredData;
        expectedResult = service.addSpeciesToCollectionIfMissing([], species);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(species);
      });

      it('should not add a Species to an array that contains it', () => {
        const species: ISpecies = sampleWithRequiredData;
        const speciesCollection: ISpecies[] = [
          {
            ...species,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addSpeciesToCollectionIfMissing(speciesCollection, species);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Species to an array that doesn't contain it", () => {
        const species: ISpecies = sampleWithRequiredData;
        const speciesCollection: ISpecies[] = [sampleWithPartialData];
        expectedResult = service.addSpeciesToCollectionIfMissing(speciesCollection, species);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(species);
      });

      it('should add only unique Species to an array', () => {
        const speciesArray: ISpecies[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const speciesCollection: ISpecies[] = [sampleWithRequiredData];
        expectedResult = service.addSpeciesToCollectionIfMissing(speciesCollection, ...speciesArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const species: ISpecies = sampleWithRequiredData;
        const species2: ISpecies = sampleWithPartialData;
        expectedResult = service.addSpeciesToCollectionIfMissing([], species, species2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(species);
        expect(expectedResult).toContain(species2);
      });

      it('should accept null and undefined values', () => {
        const species: ISpecies = sampleWithRequiredData;
        expectedResult = service.addSpeciesToCollectionIfMissing([], null, species, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(species);
      });

      it('should return initial array if no Species is added', () => {
        const speciesCollection: ISpecies[] = [sampleWithRequiredData];
        expectedResult = service.addSpeciesToCollectionIfMissing(speciesCollection, undefined, null);
        expect(expectedResult).toEqual(speciesCollection);
      });
    });

    describe('compareSpecies', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareSpecies(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareSpecies(entity1, entity2);
        const compareResult2 = service.compareSpecies(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareSpecies(entity1, entity2);
        const compareResult2 = service.compareSpecies(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareSpecies(entity1, entity2);
        const compareResult2 = service.compareSpecies(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
