package com.smartgym.manager.service.mapper;

import static com.smartgym.manager.domain.CheckInAsserts.*;
import static com.smartgym.manager.domain.CheckInTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CheckInMapperTest {

    private CheckInMapper checkInMapper;

    @BeforeEach
    void setUp() {
        checkInMapper = new CheckInMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCheckInSample1();
        var actual = checkInMapper.toEntity(checkInMapper.toDto(expected));
        assertCheckInAllPropertiesEquals(expected, actual);
    }
}
