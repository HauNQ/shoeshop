package com.tech.shoeshop.mapper;

import com.tech.shoeshop.dto.request.auth.RegisterRequest;
import com.tech.shoeshop.entity.auth.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthMapper {
    @Mapping(source = "encodedPassword", target = "password")
    User toEntity(RegisterRequest request, String encodedPassword);
}
