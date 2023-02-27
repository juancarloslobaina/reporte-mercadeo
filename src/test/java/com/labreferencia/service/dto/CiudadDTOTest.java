package com.labreferencia.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.labreferencia.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CiudadDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CiudadDTO.class);
        CiudadDTO ciudadDTO1 = new CiudadDTO();
        ciudadDTO1.setId(1L);
        CiudadDTO ciudadDTO2 = new CiudadDTO();
        assertThat(ciudadDTO1).isNotEqualTo(ciudadDTO2);
        ciudadDTO2.setId(ciudadDTO1.getId());
        assertThat(ciudadDTO1).isEqualTo(ciudadDTO2);
        ciudadDTO2.setId(2L);
        assertThat(ciudadDTO1).isNotEqualTo(ciudadDTO2);
        ciudadDTO1.setId(null);
        assertThat(ciudadDTO1).isNotEqualTo(ciudadDTO2);
    }
}
