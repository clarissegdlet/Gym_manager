package com.smartgym.manager.service.mapper;

import static com.smartgym.manager.domain.ClassSessionAsserts.*;
import static com.smartgym.manager.domain.ClassSessionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClassSessionMapperTest {

    private ClassSessionMapper classSessionMapper;

    @BeforeEach
    void setUp() {
        classSessionMapper = new ClassSessionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getClassSessionSample1();
        var actual = classSessionMapper.toEntity(classSessionMapper.toDto(expected));
        assertClassSessionAllPropertiesEquals(expected, actual);
    }
}
