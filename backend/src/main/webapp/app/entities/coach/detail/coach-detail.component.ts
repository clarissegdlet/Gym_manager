import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { ICoach } from '../coach.model';

@Component({
  selector: 'jhi-coach-detail',
  templateUrl: './coach-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class CoachDetailComponent {
  coach = input<ICoach | null>(null);

  previousState(): void {
    window.history.back();
  }
}
