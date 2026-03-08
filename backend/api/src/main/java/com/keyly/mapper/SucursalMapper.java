package com.keyly.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.keyly.model.Sucursal;
import com.keyly.model.request.SucursalRequest;

@Mapper(componentModel = "spring")
public interface SucursalMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateSucursalFromDto(SucursalRequest dto, @MappingTarget Sucursal entity);

}
