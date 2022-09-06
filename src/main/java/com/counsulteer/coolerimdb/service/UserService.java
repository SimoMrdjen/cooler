package com.counsulteer.coolerimdb.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import com.counsulteer.coolerimdb.dto.user.CreateUserDto;
import com.counsulteer.coolerimdb.dto.user.UpdateUserDto;
import com.counsulteer.coolerimdb.dto.user.UserDto;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserDto getUser(Long id);

    UserDto getUserByEmail(String email);

    List<UserDto> getUsers();

    UserDto createUser(CreateUserDto newUser);

    UserDto updateUser(UpdateUserDto user, Long id);

    UserDto getLoggedInUser();

    void deleteUser(Long id);
}
