import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICheckIn } from '../check-in.model';
import { CheckInService } from '../service/check-in.service';

const checkInResolve = (route: ActivatedRouteSnapshot): Observable<null | ICheckIn> => {
  const id = route.params.id;
  if (id) {
    return inject(CheckInService)
      .find(id)
      .pipe(
        mergeMap((checkIn: HttpResponse<ICheckIn>) => {
          if (checkIn.body) {
            return of(checkIn.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default checkInResolve;
