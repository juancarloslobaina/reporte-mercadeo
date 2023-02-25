package com.labreferencia.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.labreferencia.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CentroMedicoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CentroMedico.class);
        CentroMedico centroMedico1 = new CentroMedico();
        centroMedico1.setId(1L);
        CentroMedico centroMedico2 = new CentroMedico();
        centroMedico2.setId(centroMedico1.getId());
        assertThat(centroMedico1).isEqualTo(centroMedico2);
        centroMedico2.setId(2L);
        assertThat(centroMedico1).isNotEqualTo(centroMedico2);
        centroMedico1.setId(null);
        assertThat(centroMedico1).isNotEqualTo(centroMedico2);
    }
}
