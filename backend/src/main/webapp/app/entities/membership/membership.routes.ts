import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import MembershipResolve from './route/membership-routing-resolve.service';

const membershipRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/membership.component').then(m => m.MembershipComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/membership-detail.component').then(m => m.MembershipDetailComponent),
    resolve: {
      membership: MembershipResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/membership-update.component').then(m => m.MembershipUpdateComponent),
    resolve: {
      membership: MembershipResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/membership-update.component').then(m => m.MembershipUpdateComponent),
    resolve: {
      membership: MembershipResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default membershipRoute;
