import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ICoach } from '../coach.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../coach.test-samples';

import { CoachService } from './coach.service';

const requireRestSample: ICoach = {
  ...sampleWithRequiredData,
};

describe('Coach Service', () => {
  let service: CoachService;
  let httpMock: HttpTestingController;
  let expectedResult: ICoach | ICoach[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(CoachService);
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

    it('should create a Coach', () => {
      const coach = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(coach).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Coach', () => {
      const coach = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(coach).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Coach', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Coach', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Coach', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addCoachToCollectionIfMissing', () => {
      it('should add a Coach to an empty array', () => {
        const coach: ICoach = sampleWithRequiredData;
        expectedResult = service.addCoachToCollectionIfMissing([], coach);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(coach);
      });

      it('should not add a Coach to an array that contains it', () => {
        const coach: ICoach = sampleWithRequiredData;
        const coachCollection: ICoach[] = [
          {
            ...coach,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCoachToCollectionIfMissing(coachCollection, coach);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Coach to an array that doesn't contain it", () => {
        const coach: ICoach = sampleWithRequiredData;
        const coachCollection: ICoach[] = [sampleWithPartialData];
        expectedResult = service.addCoachToCollectionIfMissing(coachCollection, coach);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(coach);
      });

      it('should add only unique Coach to an array', () => {
        const coachArray: ICoach[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const coachCollection: ICoach[] = [sampleWithRequiredData];
        expectedResult = service.addCoachToCollectionIfMissing(coachCollection, ...coachArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const coach: ICoach = sampleWithRequiredData;
        const coach2: ICoach = sampleWithPartialData;
        expectedResult = service.addCoachToCollectionIfMissing([], coach, coach2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(coach);
        expect(expectedResult).toContain(coach2);
      });

      it('should accept null and undefined values', () => {
        const coach: ICoach = sampleWithRequiredData;
        expectedResult = service.addCoachToCollectionIfMissing([], null, coach, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(coach);
      });

      it('should return initial array if no Coach is added', () => {
        const coachCollection: ICoach[] = [sampleWithRequiredData];
        expectedResult = service.addCoachToCollectionIfMissing(coachCollection, undefined, null);
        expect(expectedResult).toEqual(coachCollection);
      });
    });

    describe('compareCoach', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCoach(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 24583 };
        const entity2 = null;

        const compareResult1 = service.compareCoach(entity1, entity2);
        const compareResult2 = service.compareCoach(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 24583 };
        const entity2 = { id: 31551 };

        const compareResult1 = service.compareCoach(entity1, entity2);
        const compareResult2 = service.compareCoach(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 24583 };
        const entity2 = { id: 24583 };

        const compareResult1 = service.compareCoach(entity1, entity2);
        const compareResult2 = service.compareCoach(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
