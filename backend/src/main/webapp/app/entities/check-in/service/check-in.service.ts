import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICheckIn, NewCheckIn } from '../check-in.model';

export type PartialUpdateCheckIn = Partial<ICheckIn> & Pick<ICheckIn, 'id'>;

type RestOf<T extends ICheckIn | NewCheckIn> = Omit<T, 'checkInTime'> & {
  checkInTime?: string | null;
};

export type RestCheckIn = RestOf<ICheckIn>;

export type NewRestCheckIn = RestOf<NewCheckIn>;

export type PartialUpdateRestCheckIn = RestOf<PartialUpdateCheckIn>;

export type EntityResponseType = HttpResponse<ICheckIn>;
export type EntityArrayResponseType = HttpResponse<ICheckIn[]>;

@Injectable({ providedIn: 'root' })
export class CheckInService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/check-ins');

  create(checkIn: NewCheckIn): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(checkIn);
    return this.http
      .post<RestCheckIn>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(checkIn: ICheckIn): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(checkIn);
    return this.http
      .put<RestCheckIn>(`${this.resourceUrl}/${this.getCheckInIdentifier(checkIn)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(checkIn: PartialUpdateCheckIn): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(checkIn);
    return this.http
      .patch<RestCheckIn>(`${this.resourceUrl}/${this.getCheckInIdentifier(checkIn)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestCheckIn>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestCheckIn[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getCheckInIdentifier(checkIn: Pick<ICheckIn, 'id'>): number {
    return checkIn.id;
  }

  compareCheckIn(o1: Pick<ICheckIn, 'id'> | null, o2: Pick<ICheckIn, 'id'> | null): boolean {
    return o1 && o2 ? this.getCheckInIdentifier(o1) === this.getCheckInIdentifier(o2) : o1 === o2;
  }

  addCheckInToCollectionIfMissing<Type extends Pick<ICheckIn, 'id'>>(
    checkInCollection: Type[],
    ...checkInsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const checkIns: Type[] = checkInsToCheck.filter(isPresent);
    if (checkIns.length > 0) {
      const checkInCollectionIdentifiers = checkInCollection.map(checkInItem => this.getCheckInIdentifier(checkInItem));
      const checkInsToAdd = checkIns.filter(checkInItem => {
        const checkInIdentifier = this.getCheckInIdentifier(checkInItem);
        if (checkInCollectionIdentifiers.includes(checkInIdentifier)) {
          return false;
        }
        checkInCollectionIdentifiers.push(checkInIdentifier);
        return true;
      });
      return [...checkInsToAdd, ...checkInCollection];
    }
    return checkInCollection;
  }

  protected convertDateFromClient<T extends ICheckIn | NewCheckIn | PartialUpdateCheckIn>(checkIn: T): RestOf<T> {
    return {
      ...checkIn,
      checkInTime: checkIn.checkInTime?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restCheckIn: RestCheckIn): ICheckIn {
    return {
      ...restCheckIn,
      checkInTime: restCheckIn.checkInTime ? dayjs(restCheckIn.checkInTime) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestCheckIn>): HttpResponse<ICheckIn> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestCheckIn[]>): HttpResponse<ICheckIn[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
