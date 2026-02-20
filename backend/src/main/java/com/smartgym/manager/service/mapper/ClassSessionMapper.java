package com.smartgym.manager.service.mapper;

import com.smartgym.manager.domain.ClassSession;
import com.smartgym.manager.service.dto.ClassSessionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ClassSession} and its DTO {@link ClassSessionDTO}.
 */
@Mapper(componentModel = "spring")
public interface ClassSessionMapper extends EntityMapper<ClassSessionDTO, ClassSession> {}
