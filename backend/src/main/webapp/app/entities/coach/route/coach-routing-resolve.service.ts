import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICoach } from '../coach.model';
import { CoachService } from '../service/coach.service';

const coachResolve = (route: ActivatedRouteSnapshot): Observable<null | ICoach> => {
  const id = route.params.id;
  if (id) {
    return inject(CoachService)
      .find(id)
      .pipe(
        mergeMap((coach: HttpResponse<ICoach>) => {
          if (coach.body) {
            return of(coach.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default coachResolve;
