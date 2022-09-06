package com.counsulteer.coolerimdb.mapper;

import com.counsulteer.coolerimdb.dto.user.CreateUserDto;
import com.counsulteer.coolerimdb.dto.user.UpdateUserDto;
import com.counsulteer.coolerimdb.dto.user.UserDto;
import com.counsulteer.coolerimdb.entity.User;

import java.util.Objects;

public class UserMapper {

    public User mapCreateDtoToEntity(CreateUserDto createUserDTO) {
        return new User(null, createUserDTO.getFirstName(), createUserDTO.getLastName(), createUserDTO.getEmail(), createUserDTO.getPassword());
    }

    public UserDto mapEntityToDto(User user) {
        return new UserDto(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail());
    }

    public void updateUser(UpdateUserDto updateUserDto, User user) {
        if(Objects.nonNull(updateUserDto.getFirstName()))
            user.setFirstName(updateUserDto.getFirstName());

        if(Objects.nonNull(updateUserDto.getLastName()))
            user.setLastName(updateUserDto.getLastName());

        if(Objects.nonNull(updateUserDto.getEmail()))
            user.setEmail(updateUserDto.getEmail());
    }
}
