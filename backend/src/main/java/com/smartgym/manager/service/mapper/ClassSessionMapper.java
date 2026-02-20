package com.smartgym.manager.service.mapper;

import com.smartgym.manager.domain.ClassSession;
import com.smartgym.manager.domain.Coach;
import com.smartgym.manager.domain.Room;
import com.smartgym.manager.service.dto.ClassSessionDTO;
import com.smartgym.manager.service.dto.CoachDTO;
import com.smartgym.manager.service.dto.RoomDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ClassSession} and its DTO {@link ClassSessionDTO}.
 */
@Mapper(componentModel = "spring")
public interface ClassSessionMapper extends EntityMapper<ClassSessionDTO, ClassSession> {
    @Mapping(target = "coach", source = "coach", qualifiedByName = "coachName")
    @Mapping(target = "room", source = "room", qualifiedByName = "roomName")
    ClassSessionDTO toDto(ClassSession s);

    @Named("coachName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    CoachDTO toDtoCoachName(Coach coach);

    @Named("roomName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    RoomDTO toDtoRoomName(Room room);
}
