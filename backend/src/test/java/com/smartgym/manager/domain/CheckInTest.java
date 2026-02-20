package com.smartgym.manager.domain;

import static com.smartgym.manager.domain.CheckInTestSamples.*;
import static com.smartgym.manager.domain.MemberTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.smartgym.manager.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CheckInTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CheckIn.class);
        CheckIn checkIn1 = getCheckInSample1();
        CheckIn checkIn2 = new CheckIn();
        assertThat(checkIn1).isNotEqualTo(checkIn2);

        checkIn2.setId(checkIn1.getId());
        assertThat(checkIn1).isEqualTo(checkIn2);

        checkIn2 = getCheckInSample2();
        assertThat(checkIn1).isNotEqualTo(checkIn2);
    }

    @Test
    void memberTest() {
        CheckIn checkIn = getCheckInRandomSampleGenerator();
        Member memberBack = getMemberRandomSampleGenerator();

        checkIn.setMember(memberBack);
        assertThat(checkIn.getMember()).isEqualTo(memberBack);

        checkIn.member(null);
        assertThat(checkIn.getMember()).isNull();
    }
}
