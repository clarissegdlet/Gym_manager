import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { MembershipDetailComponent } from './membership-detail.component';

describe('Membership Management Detail Component', () => {
  let comp: MembershipDetailComponent;
  let fixture: ComponentFixture<MembershipDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MembershipDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./membership-detail.component').then(m => m.MembershipDetailComponent),
              resolve: { membership: () => of({ id: 10393 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(MembershipDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MembershipDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load membership on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', MembershipDetailComponent);

      // THEN
      expect(instance.membership()).toEqual(expect.objectContaining({ id: 10393 }));
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
