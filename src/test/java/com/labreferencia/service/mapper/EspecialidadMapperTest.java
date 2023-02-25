package com.labreferencia.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EspecialidadMapperTest {

    private EspecialidadMapper especialidadMapper;

    @BeforeEach
    public void setUp() {
        especialidadMapper = new EspecialidadMapperImpl();
    }
}
