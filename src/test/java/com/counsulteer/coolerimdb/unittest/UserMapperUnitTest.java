package com.counsulteer.coolerimdb.unittest;

import com.counsulteer.coolerimdb.dto.user.CreateUserDto;
import com.counsulteer.coolerimdb.dto.user.UpdateUserDto;
import com.counsulteer.coolerimdb.dto.user.UserDto;
import com.counsulteer.coolerimdb.entity.User;
import com.counsulteer.coolerimdb.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserMapperUnitTest {
    private final UserMapper userMapper = new UserMapper();
    private CreateUserDto createUserDto;
    private User user;
    private UserDto userDto;

    @BeforeEach
    void beforeEach() {
        createUserDto = new CreateUserDto("Čvarkov", "Đorđe", "cvarkovibaba@pejicevisalasi.rs", "divljibucak");
        user = new User(1L, "Čvarkov", "Đorđe", "cvarkovibaba@pejicevisalasi.rs", "divljibucak");
        userDto = new UserDto(1L, "Čvarkov", "Đorđe", "cvarkovibaba@pejicevisalasi.rs");
    }

    @Test
    public void shouldMapToDtoWhenMapToDtoEntityCalled() {
        assertEquals(userDto, userMapper.mapEntityToDto(user));
    }

    @Test
    public void shouldMapToEntityWhenMapToCreateDtoEntityCalled() {
        user.setId(null);
        assertEquals(user, userMapper.mapCreateDtoToEntity(createUserDto));
    }

    @Test
    public void shouldUpdateUserWhenUpdateUserUpdateDtoCalled() {
        UpdateUserDto updateUserDto = new UpdateUserDto("Dragan", "Torbica", "dragotorbica@.rs");
        User alteredUser = new User(1L, "Dragan", "Torbica", "dragotorbica@.rs", "divljibucak");
        userMapper.updateUser(updateUserDto, user);
        assertEquals(alteredUser, user);
    }


}
