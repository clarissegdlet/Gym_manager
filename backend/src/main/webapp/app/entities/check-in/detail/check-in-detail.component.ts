import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { ICheckIn } from '../check-in.model';

@Component({
  selector: 'jhi-check-in-detail',
  templateUrl: './check-in-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class CheckInDetailComponent {
  checkIn = input<ICheckIn | null>(null);

  previousState(): void {
    window.history.back();
  }
}
