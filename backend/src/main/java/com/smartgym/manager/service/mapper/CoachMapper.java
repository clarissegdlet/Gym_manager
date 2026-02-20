package com.smartgym.manager.service.mapper;

import com.smartgym.manager.domain.Coach;
import com.smartgym.manager.service.dto.CoachDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Coach} and its DTO {@link CoachDTO}.
 */
@Mapper(componentModel = "spring")
public interface CoachMapper extends EntityMapper<CoachDTO, Coach> {}
