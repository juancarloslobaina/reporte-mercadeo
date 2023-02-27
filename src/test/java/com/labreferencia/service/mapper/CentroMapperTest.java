package com.labreferencia.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CentroMapperTest {

    private CentroMapper centroMapper;

    @BeforeEach
    public void setUp() {
        centroMapper = new CentroMapperImpl();
    }
}
