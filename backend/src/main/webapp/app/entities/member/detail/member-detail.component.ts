import { Component, input, inject, OnInit, effect } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IMember } from '../member.model';
import { CheckInService } from 'app/entities/check-in/service/check-in.service';
import { MembershipService } from 'app/entities/membership/service/membership.service';

@Component({
  selector: 'jhi-member-detail',
  templateUrl: './member-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class MemberDetailComponent implements OnInit {
  member = input<IMember | null>(null);

  protected checkInService = inject(CheckInService);
  protected membershipService = inject(MembershipService);

  visitCount = 0;
  subscriptionValid = false;

  ngOnInit(): void {
    effect(() => {
      const m = this.member();

      if (m?.id) {
        this.loadStats(m.id);
      }
    });
  }

  private loadStats(memberId: number): void {
    // 🔢 nombre de visites
    this.checkInService.query({ 'memberId.equals': memberId }).subscribe(res => {
      this.visitCount = res.body?.length ?? 0;
    });

    // 💳 abonnement actif
    this.membershipService.query({ 'memberId.equals': memberId }).subscribe(res => {
      const memberships = res.body ?? [];
      this.subscriptionValid = memberships.some(m => m.status === 'ACTIVE');
    });
  }

  previousState(): void {
    window.history.back();
  }
}
