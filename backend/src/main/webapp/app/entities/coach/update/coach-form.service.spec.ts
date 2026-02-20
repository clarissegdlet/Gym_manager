import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../coach.test-samples';

import { CoachFormService } from './coach-form.service';

describe('Coach Form Service', () => {
  let service: CoachFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CoachFormService);
  });

  describe('Service methods', () => {
    describe('createCoachFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCoachFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            specialty: expect.any(Object),
          }),
        );
      });

      it('passing ICoach should create a new form with FormGroup', () => {
        const formGroup = service.createCoachFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            specialty: expect.any(Object),
          }),
        );
      });
    });

    describe('getCoach', () => {
      it('should return NewCoach for default Coach initial value', () => {
        const formGroup = service.createCoachFormGroup(sampleWithNewData);

        const coach = service.getCoach(formGroup) as any;

        expect(coach).toMatchObject(sampleWithNewData);
      });

      it('should return NewCoach for empty Coach initial value', () => {
        const formGroup = service.createCoachFormGroup();

        const coach = service.getCoach(formGroup) as any;

        expect(coach).toMatchObject({});
      });

      it('should return ICoach', () => {
        const formGroup = service.createCoachFormGroup(sampleWithRequiredData);

        const coach = service.getCoach(formGroup) as any;

        expect(coach).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICoach should not enable id FormControl', () => {
        const formGroup = service.createCoachFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCoach should disable id FormControl', () => {
        const formGroup = service.createCoachFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
