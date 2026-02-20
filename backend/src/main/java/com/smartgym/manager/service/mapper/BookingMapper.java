package com.smartgym.manager.service.mapper;

import com.smartgym.manager.domain.Booking;
import com.smartgym.manager.domain.ClassSession;
import com.smartgym.manager.domain.Member;
import com.smartgym.manager.service.dto.BookingDTO;
import com.smartgym.manager.service.dto.ClassSessionDTO;
import com.smartgym.manager.service.dto.MemberDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Booking} and its DTO {@link BookingDTO}.
 */
@Mapper(componentModel = "spring")
public interface BookingMapper extends EntityMapper<BookingDTO, Booking> {
    @Mapping(target = "member", source = "member", qualifiedByName = "memberId")
    @Mapping(target = "classSession", source = "classSession", qualifiedByName = "classSessionId")
    BookingDTO toDto(Booking s);

    @Named("memberId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MemberDTO toDtoMemberId(Member member);

    @Named("classSessionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ClassSessionDTO toDtoClassSessionId(ClassSession classSession);
}
