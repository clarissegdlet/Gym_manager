package com.smartgym.manager.service.mapper;

import static com.smartgym.manager.domain.CoachAsserts.*;
import static com.smartgym.manager.domain.CoachTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CoachMapperTest {

    private CoachMapper coachMapper;

    @BeforeEach
    void setUp() {
        coachMapper = new CoachMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCoachSample1();
        var actual = coachMapper.toEntity(coachMapper.toDto(expected));
        assertCoachAllPropertiesEquals(expected, actual);
    }
}
