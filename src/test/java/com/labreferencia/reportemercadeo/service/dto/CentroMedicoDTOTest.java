package com.labreferencia.reportemercadeo.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.labreferencia.reportemercadeo.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CentroMedicoDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CentroMedicoDTO.class);
        CentroMedicoDTO centroMedicoDTO1 = new CentroMedicoDTO();
        centroMedicoDTO1.setId(1L);
        CentroMedicoDTO centroMedicoDTO2 = new CentroMedicoDTO();
        assertThat(centroMedicoDTO1).isNotEqualTo(centroMedicoDTO2);
        centroMedicoDTO2.setId(centroMedicoDTO1.getId());
        assertThat(centroMedicoDTO1).isEqualTo(centroMedicoDTO2);
        centroMedicoDTO2.setId(2L);
        assertThat(centroMedicoDTO1).isNotEqualTo(centroMedicoDTO2);
        centroMedicoDTO1.setId(null);
        assertThat(centroMedicoDTO1).isNotEqualTo(centroMedicoDTO2);
    }
}
