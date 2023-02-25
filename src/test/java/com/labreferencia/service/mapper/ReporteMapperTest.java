package com.labreferencia.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReporteMapperTest {

    private ReporteMapper reporteMapper;

    @BeforeEach
    public void setUp() {
        reporteMapper = new ReporteMapperImpl();
    }
}
