package com.labreferencia.reportemercadeo.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CiudadMapperTest {

    private CiudadMapper ciudadMapper;

    @BeforeEach
    public void setUp() {
        ciudadMapper = new CiudadMapperImpl();
    }
}
