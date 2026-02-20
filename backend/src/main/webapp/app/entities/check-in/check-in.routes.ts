import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import CheckInResolve from './route/check-in-routing-resolve.service';

const checkInRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/check-in.component').then(m => m.CheckInComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/check-in-detail.component').then(m => m.CheckInDetailComponent),
    resolve: {
      checkIn: CheckInResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/check-in-update.component').then(m => m.CheckInUpdateComponent),
    resolve: {
      checkIn: CheckInResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/check-in-update.component').then(m => m.CheckInUpdateComponent),
    resolve: {
      checkIn: CheckInResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default checkInRoute;
