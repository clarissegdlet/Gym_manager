import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICoach, NewCoach } from '../coach.model';

export type PartialUpdateCoach = Partial<ICoach> & Pick<ICoach, 'id'>;

export type EntityResponseType = HttpResponse<ICoach>;
export type EntityArrayResponseType = HttpResponse<ICoach[]>;

@Injectable({ providedIn: 'root' })
export class CoachService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/coaches');

  create(coach: NewCoach): Observable<EntityResponseType> {
    return this.http.post<ICoach>(this.resourceUrl, coach, { observe: 'response' });
  }

  update(coach: ICoach): Observable<EntityResponseType> {
    return this.http.put<ICoach>(`${this.resourceUrl}/${this.getCoachIdentifier(coach)}`, coach, { observe: 'response' });
  }

  partialUpdate(coach: PartialUpdateCoach): Observable<EntityResponseType> {
    return this.http.patch<ICoach>(`${this.resourceUrl}/${this.getCoachIdentifier(coach)}`, coach, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICoach>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICoach[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getCoachIdentifier(coach: Pick<ICoach, 'id'>): number {
    return coach.id;
  }

  compareCoach(o1: Pick<ICoach, 'id'> | null, o2: Pick<ICoach, 'id'> | null): boolean {
    return o1 && o2 ? this.getCoachIdentifier(o1) === this.getCoachIdentifier(o2) : o1 === o2;
  }

  addCoachToCollectionIfMissing<Type extends Pick<ICoach, 'id'>>(
    coachCollection: Type[],
    ...coachesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const coaches: Type[] = coachesToCheck.filter(isPresent);
    if (coaches.length > 0) {
      const coachCollectionIdentifiers = coachCollection.map(coachItem => this.getCoachIdentifier(coachItem));
      const coachesToAdd = coaches.filter(coachItem => {
        const coachIdentifier = this.getCoachIdentifier(coachItem);
        if (coachCollectionIdentifiers.includes(coachIdentifier)) {
          return false;
        }
        coachCollectionIdentifiers.push(coachIdentifier);
        return true;
      });
      return [...coachesToAdd, ...coachCollection];
    }
    return coachCollection;
  }
}
