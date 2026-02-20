package com.smartgym.manager.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.smartgym.manager.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ClassSessionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClassSessionDTO.class);
        ClassSessionDTO classSessionDTO1 = new ClassSessionDTO();
        classSessionDTO1.setId(1L);
        ClassSessionDTO classSessionDTO2 = new ClassSessionDTO();
        assertThat(classSessionDTO1).isNotEqualTo(classSessionDTO2);
        classSessionDTO2.setId(classSessionDTO1.getId());
        assertThat(classSessionDTO1).isEqualTo(classSessionDTO2);
        classSessionDTO2.setId(2L);
        assertThat(classSessionDTO1).isNotEqualTo(classSessionDTO2);
        classSessionDTO1.setId(null);
        assertThat(classSessionDTO1).isNotEqualTo(classSessionDTO2);
    }
}
