package com.labreferencia.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.labreferencia.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CentroTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Centro.class);
        Centro centro1 = new Centro();
        centro1.setId(1L);
        Centro centro2 = new Centro();
        centro2.setId(centro1.getId());
        assertThat(centro1).isEqualTo(centro2);
        centro2.setId(2L);
        assertThat(centro1).isNotEqualTo(centro2);
        centro1.setId(null);
        assertThat(centro1).isNotEqualTo(centro2);
    }
}
