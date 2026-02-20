package com.smartgym.manager.service.mapper;

import com.smartgym.manager.domain.Room;
import com.smartgym.manager.service.dto.RoomDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Room} and its DTO {@link RoomDTO}.
 */
@Mapper(componentModel = "spring")
public interface RoomMapper extends EntityMapper<RoomDTO, Room> {}
