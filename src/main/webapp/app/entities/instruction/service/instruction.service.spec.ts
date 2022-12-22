import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IInstruction } from '../instruction.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../instruction.test-samples';

import { InstructionService } from './instruction.service';

const requireRestSample: IInstruction = {
  ...sampleWithRequiredData,
};

describe('Instruction Service', () => {
  let service: InstructionService;
  let httpMock: HttpTestingController;
  let expectedResult: IInstruction | IInstruction[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(InstructionService);
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

    it('should create a Instruction', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const instruction = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(instruction).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Instruction', () => {
      const instruction = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(instruction).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Instruction', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Instruction', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Instruction', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addInstructionToCollectionIfMissing', () => {
      it('should add a Instruction to an empty array', () => {
        const instruction: IInstruction = sampleWithRequiredData;
        expectedResult = service.addInstructionToCollectionIfMissing([], instruction);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(instruction);
      });

      it('should not add a Instruction to an array that contains it', () => {
        const instruction: IInstruction = sampleWithRequiredData;
        const instructionCollection: IInstruction[] = [
          {
            ...instruction,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addInstructionToCollectionIfMissing(instructionCollection, instruction);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Instruction to an array that doesn't contain it", () => {
        const instruction: IInstruction = sampleWithRequiredData;
        const instructionCollection: IInstruction[] = [sampleWithPartialData];
        expectedResult = service.addInstructionToCollectionIfMissing(instructionCollection, instruction);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(instruction);
      });

      it('should add only unique Instruction to an array', () => {
        const instructionArray: IInstruction[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const instructionCollection: IInstruction[] = [sampleWithRequiredData];
        expectedResult = service.addInstructionToCollectionIfMissing(instructionCollection, ...instructionArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const instruction: IInstruction = sampleWithRequiredData;
        const instruction2: IInstruction = sampleWithPartialData;
        expectedResult = service.addInstructionToCollectionIfMissing([], instruction, instruction2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(instruction);
        expect(expectedResult).toContain(instruction2);
      });

      it('should accept null and undefined values', () => {
        const instruction: IInstruction = sampleWithRequiredData;
        expectedResult = service.addInstructionToCollectionIfMissing([], null, instruction, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(instruction);
      });

      it('should return initial array if no Instruction is added', () => {
        const instructionCollection: IInstruction[] = [sampleWithRequiredData];
        expectedResult = service.addInstructionToCollectionIfMissing(instructionCollection, undefined, null);
        expect(expectedResult).toEqual(instructionCollection);
      });
    });

    describe('compareInstruction', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareInstruction(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareInstruction(entity1, entity2);
        const compareResult2 = service.compareInstruction(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareInstruction(entity1, entity2);
        const compareResult2 = service.compareInstruction(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareInstruction(entity1, entity2);
        const compareResult2 = service.compareInstruction(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
