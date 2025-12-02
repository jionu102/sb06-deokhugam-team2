package com.codeit.sb06deokhugamteam2.user.mapper;

import com.codeit.sb06deokhugamteam2.user.entity.User;
import com.codeit.sb06deokhugamteam2.user.dto.UserDto;
import com.codeit.sb06deokhugamteam2.user.dto.UserRegisterRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
//    @Mapping(target = "reviews", ignore = true)
//    @Mapping(target = "comments", ignore = true)
//    @Mapping(target = "reviewLikes", ignore = true)
    User toEntity(UserRegisterRequest request);

    UserDto toDto(User user);
}