import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IMembership } from '../membership.model';
import { MembershipService } from '../service/membership.service';

const membershipResolve = (route: ActivatedRouteSnapshot): Observable<null | IMembership> => {
  const id = route.params.id;
  if (id) {
    return inject(MembershipService)
      .find(id)
      .pipe(
        mergeMap((membership: HttpResponse<IMembership>) => {
          if (membership.body) {
            return of(membership.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default membershipResolve;
