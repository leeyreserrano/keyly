package com.keyly.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.keyly.model.Usuari;
import com.keyly.model.request.UsuariRequest;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { SucursalMapper.class,
        DepartamentMapper.class, RolMapper.class })
public abstract class UsuariMapper {

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Mapping(target = "kdfSalt", expression = "java(dto.kdfSalt() != null ? java.util.Base64.getDecoder().decode(dto.kdfSalt()) : entity.getKdfSalt())")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateUsuariFromDto(UsuariRequest dto, @MappingTarget Usuari entity);

    @AfterMapping
    protected void encodePassword(UsuariRequest dto, @MappingTarget Usuari entity) {
        if (dto.contrasenya() != null) {
            entity.setContrasenya(passwordEncoder.encode(dto.contrasenya()));
        }
    }

}
