import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { CheckInDetailComponent } from './check-in-detail.component';

describe('CheckIn Management Detail Component', () => {
  let comp: CheckInDetailComponent;
  let fixture: ComponentFixture<CheckInDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CheckInDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./check-in-detail.component').then(m => m.CheckInDetailComponent),
              resolve: { checkIn: () => of({ id: 14585 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(CheckInDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CheckInDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load checkIn on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', CheckInDetailComponent);

      // THEN
      expect(instance.checkIn()).toEqual(expect.objectContaining({ id: 14585 }));
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
