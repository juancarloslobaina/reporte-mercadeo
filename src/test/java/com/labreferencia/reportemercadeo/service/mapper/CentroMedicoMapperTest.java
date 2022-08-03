package com.labreferencia.reportemercadeo.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CentroMedicoMapperTest {

    private CentroMedicoMapper centroMedicoMapper;

    @BeforeEach
    public void setUp() {
        centroMedicoMapper = new CentroMedicoMapperImpl();
    }
}
