import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { CoachDetailComponent } from './coach-detail.component';

describe('Coach Management Detail Component', () => {
  let comp: CoachDetailComponent;
  let fixture: ComponentFixture<CoachDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CoachDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./coach-detail.component').then(m => m.CoachDetailComponent),
              resolve: { coach: () => of({ id: 24583 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(CoachDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CoachDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load coach on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', CoachDetailComponent);

      // THEN
      expect(instance.coach()).toEqual(expect.objectContaining({ id: 24583 }));
    });
  });

  describe('PreviousState', () => {
    it('should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
