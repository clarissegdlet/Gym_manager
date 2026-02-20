package com.smartgym.manager.service.mapper;

import com.smartgym.manager.domain.Membership;
import com.smartgym.manager.domain.Payment;
import com.smartgym.manager.service.dto.MembershipDTO;
import com.smartgym.manager.service.dto.PaymentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Payment} and its DTO {@link PaymentDTO}.
 */
@Mapper(componentModel = "spring")
public interface PaymentMapper extends EntityMapper<PaymentDTO, Payment> {
    @Mapping(target = "membership", source = "membership", qualifiedByName = "membershipId")
    PaymentDTO toDto(Payment s);

    @Named("membershipId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MembershipDTO toDtoMembershipId(Membership membership);
}
