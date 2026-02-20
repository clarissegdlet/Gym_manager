import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IMember, NewMember } from '../member.model';

export type PartialUpdateMember = Partial<IMember> & Pick<IMember, 'id'>;

export type EntityResponseType = HttpResponse<IMember>;
export type EntityArrayResponseType = HttpResponse<IMember[]>;

@Injectable({ providedIn: 'root' })
export class MemberService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/members');

  create(member: NewMember): Observable<EntityResponseType> {
    return this.http.post<IMember>(this.resourceUrl, member, { observe: 'response' });
  }

  update(member: IMember): Observable<EntityResponseType> {
    return this.http.put<IMember>(`${this.resourceUrl}/${this.getMemberIdentifier(member)}`, member, { observe: 'response' });
  }

  partialUpdate(member: PartialUpdateMember): Observable<EntityResponseType> {
    return this.http.patch<IMember>(`${this.resourceUrl}/${this.getMemberIdentifier(member)}`, member, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IMember>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IMember[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getMemberIdentifier(member: Pick<IMember, 'id'>): number {
    return member.id;
  }

  compareMember(o1: Pick<IMember, 'id'> | null, o2: Pick<IMember, 'id'> | null): boolean {
    return o1 && o2 ? this.getMemberIdentifier(o1) === this.getMemberIdentifier(o2) : o1 === o2;
  }

  addMemberToCollectionIfMissing<Type extends Pick<IMember, 'id'>>(
    memberCollection: Type[],
    ...membersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const members: Type[] = membersToCheck.filter(isPresent);
    if (members.length > 0) {
      const memberCollectionIdentifiers = memberCollection.map(memberItem => this.getMemberIdentifier(memberItem));
      const membersToAdd = members.filter(memberItem => {
        const memberIdentifier = this.getMemberIdentifier(memberItem);
        if (memberCollectionIdentifiers.includes(memberIdentifier)) {
          return false;
        }
        memberCollectionIdentifiers.push(memberIdentifier);
        return true;
      });
      return [...membersToAdd, ...memberCollection];
    }
    return memberCollection;
  }
}
