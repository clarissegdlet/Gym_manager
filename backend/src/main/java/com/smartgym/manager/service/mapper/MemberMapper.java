package com.smartgym.manager.service.mapper;

import com.smartgym.manager.domain.Member;
import com.smartgym.manager.service.dto.MemberDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Member} and its DTO {@link MemberDTO}.
 */
@Mapper(componentModel = "spring")
public interface MemberMapper extends EntityMapper<MemberDTO, Member> {}
