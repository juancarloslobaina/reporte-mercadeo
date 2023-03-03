package com.labreferencia.service.mapper;

import com.labreferencia.domain.Centro;
import com.labreferencia.domain.Ciudad;
import com.labreferencia.domain.User;
import com.labreferencia.service.dto.CentroDTO;
import com.labreferencia.service.dto.CiudadDTO;
import com.labreferencia.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Centro} and its DTO {@link CentroDTO}.
 */
@Mapper(componentModel = "spring")
public interface CentroMapper extends EntityMapper<CentroDTO, Centro> {
    @Mapping(target = "ciudad", source = "ciudad", qualifiedByName = "ciudadNombre")
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    CentroDTO toDto(Centro s);

    @Named("ciudadNombre")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nombre", source = "nombre")
    CiudadDTO toDtoCiudadNombre(Ciudad ciudad);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
