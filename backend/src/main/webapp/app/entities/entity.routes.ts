import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'smartGymManagerApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'member',
    data: { pageTitle: 'smartGymManagerApp.member.home.title' },
    loadChildren: () => import('./member/member.routes'),
  },
  {
    path: 'membership',
    data: { pageTitle: 'smartGymManagerApp.membership.home.title' },
    loadChildren: () => import('./membership/membership.routes'),
  },
  {
    path: 'payment',
    data: { pageTitle: 'smartGymManagerApp.payment.home.title' },
    loadChildren: () => import('./payment/payment.routes'),
  },
  {
    path: 'class-session',
    data: { pageTitle: 'smartGymManagerApp.classSession.home.title' },
    loadChildren: () => import('./class-session/class-session.routes'),
  },
  {
    path: 'booking',
    data: { pageTitle: 'smartGymManagerApp.booking.home.title' },
    loadChildren: () => import('./booking/booking.routes'),
  },
  {
    path: 'check-in',
    data: { pageTitle: 'smartGymManagerApp.checkIn.home.title' },
    loadChildren: () => import('./check-in/check-in.routes'),
  },
  {
    path: 'coach',
    data: { pageTitle: 'smartGymManagerApp.coach.home.title' },
    loadChildren: () => import('./coach/coach.routes'),
  },
  {
    path: 'room',
    data: { pageTitle: 'smartGymManagerApp.room.home.title' },
    loadChildren: () => import('./room/room.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
