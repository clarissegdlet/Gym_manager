package com.smartgym.manager.domain;

import static com.smartgym.manager.domain.CoachTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.smartgym.manager.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CoachTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Coach.class);
        Coach coach1 = getCoachSample1();
        Coach coach2 = new Coach();
        assertThat(coach1).isNotEqualTo(coach2);

        coach2.setId(coach1.getId());
        assertThat(coach1).isEqualTo(coach2);

        coach2 = getCoachSample2();
        assertThat(coach1).isNotEqualTo(coach2);
    }
}
