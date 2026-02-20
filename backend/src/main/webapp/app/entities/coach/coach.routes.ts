import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import CoachResolve from './route/coach-routing-resolve.service';

const coachRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/coach.component').then(m => m.CoachComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/coach-detail.component').then(m => m.CoachDetailComponent),
    resolve: {
      coach: CoachResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/coach-update.component').then(m => m.CoachUpdateComponent),
    resolve: {
      coach: CoachResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/coach-update.component').then(m => m.CoachUpdateComponent),
    resolve: {
      coach: CoachResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default coachRoute;
