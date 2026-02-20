package com.smartgym.manager.service.mapper;

import com.smartgym.manager.domain.Member;
import com.smartgym.manager.domain.Membership;
import com.smartgym.manager.service.dto.MemberDTO;
import com.smartgym.manager.service.dto.MembershipDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Membership} and its DTO {@link MembershipDTO}.
 */
@Mapper(componentModel = "spring")
public interface MembershipMapper extends EntityMapper<MembershipDTO, Membership> {
    @Mapping(target = "member", source = "member", qualifiedByName = "memberId")
    MembershipDTO toDto(Membership s);

    @Named("memberId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MemberDTO toDtoMemberId(Member member);
}
