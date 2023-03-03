package com.labreferencia.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.labreferencia.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CentroDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CentroDTO.class);
        CentroDTO centroDTO1 = new CentroDTO();
        centroDTO1.setId(1L);
        CentroDTO centroDTO2 = new CentroDTO();
        assertThat(centroDTO1).isNotEqualTo(centroDTO2);
        centroDTO2.setId(centroDTO1.getId());
        assertThat(centroDTO1).isEqualTo(centroDTO2);
        centroDTO2.setId(2L);
        assertThat(centroDTO1).isNotEqualTo(centroDTO2);
        centroDTO1.setId(null);
        assertThat(centroDTO1).isNotEqualTo(centroDTO2);
    }
}
