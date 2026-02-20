package com.smartgym.manager.service.mapper;

import static com.smartgym.manager.domain.MemberAsserts.*;
import static com.smartgym.manager.domain.MemberTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MemberMapperTest {

    private MemberMapper memberMapper;

    @BeforeEach
    void setUp() {
        memberMapper = new MemberMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMemberSample1();
        var actual = memberMapper.toEntity(memberMapper.toDto(expected));
        assertMemberAllPropertiesEquals(expected, actual);
    }
}
