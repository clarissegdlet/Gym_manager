package com.smartgym.manager.service.mapper;

import com.smartgym.manager.domain.CheckIn;
import com.smartgym.manager.domain.Member;
import com.smartgym.manager.service.dto.CheckInDTO;
import com.smartgym.manager.service.dto.MemberDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CheckIn} and its DTO {@link CheckInDTO}.
 */
@Mapper(componentModel = "spring")
public interface CheckInMapper extends EntityMapper<CheckInDTO, CheckIn> {
    @Mapping(target = "member", source = "member", qualifiedByName = "memberId")
    CheckInDTO toDto(CheckIn s);

    @Named("memberId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MemberDTO toDtoMemberId(Member member);
}
