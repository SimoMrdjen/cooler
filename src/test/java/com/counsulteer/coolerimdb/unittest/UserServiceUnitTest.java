package com.counsulteer.coolerimdb.unittest;

import com.counsulteer.coolerimdb.dto.user.CreateUserDto;
import com.counsulteer.coolerimdb.dto.user.UpdateUserDto;
import com.counsulteer.coolerimdb.dto.user.UserDto;
import com.counsulteer.coolerimdb.entity.User;
import com.counsulteer.coolerimdb.exception.BadRequestException;
import com.counsulteer.coolerimdb.exception.NotFoundException;
import com.counsulteer.coolerimdb.mapper.UserMapper;
import com.counsulteer.coolerimdb.repository.UserRepository;
import com.counsulteer.coolerimdb.service.UserService;
import com.counsulteer.coolerimdb.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {

    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final UserMapper userMapper = Mockito.mock(UserMapper.class);
    private final PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
    private final UserService userService = new UserServiceImpl(userRepository, userMapper, passwordEncoder);

    private CreateUserDto createUserDTO;
    private User user;
    private UserDto userDTO;
    private UpdateUserDto updateUserDto;

    @BeforeEach
    void beforeEach() {
        createUserDTO = new CreateUserDto("Čvarkov", "Đorđe", "cvarkovibaba@pejicevisalasi.rs", "divljibucak");
        user = new User(1L, "Čvarkov", "Đorđe", "cvarkovibaba@pejicevisalasi.rs", "divljibucak");
        userDTO = new UserDto(1L, "Čvarkov", "Đorđe", "cvarkovibaba@pejicevisalasi.rs");
        updateUserDto = new UpdateUserDto("Dragan", "Torbica", "dragotorbica@gmail.com");
    }

    @Test
    public void shouldReturnUserWhenGetUserCalled() {
        when(userMapper.mapEntityToDto(user)).thenReturn(userDTO);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        UserDto requestedUser = userService.getUser(user.getId());
        assertEquals(userDTO, requestedUser);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenGetUserIdNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        Exception exception = assertThrows(NotFoundException.class, () -> userService.getUser(user.getId()));
        assertEquals("User not found!", exception.getMessage());
    }

    @Test
    public void shouldReturnUsersWhenGetUsersCalled() {
        List<User> users = List.of(new User(1L, "Čvarkov", "Đorđe", "cvarkovibaba@pejicevisalasi.rs", "divljibucak"), new User(2L, "Dragan", "Torbica", "dragotorbica@.rs", "divljibucak"));
        User user1 = new User(1L, "Čvarkov", "Đorđe", "cvarkovibaba@pejicevisalasi.rs", "divljibucak");
        User user2 = new User(2L, "Dragan", "Torbica", "dragotorbica@.rs", "divljibucak");
        List<UserDto> usersDto = List.of(new UserDto(1L, "Čvarkov", "Đorđe", "cvarkovibaba@pejicevisalasi.rs"), new UserDto(2L, "Dragan", "Torbica", "dragotorbica@.rs"));
        UserDto userDto1 = new UserDto(1L, "Čvarkov", "Đorđe", "cvarkovibaba@pejicevisalasi.rs");
        UserDto userDto2 = new UserDto(2L, "Dragan", "Torbica", "dragotorbica@.rs");
        when(userMapper.mapEntityToDto(user1)).thenReturn(userDto1);
        when(userMapper.mapEntityToDto(user2)).thenReturn(userDto2);
        when(userRepository.findAll()).thenReturn(users);
        assertEquals(usersDto, userService.getUsers());
    }

    @Test
    public void shouldReturnCreatedUserWhenCreateUserCalled() {
        CreateUserDto createUserDTO = new CreateUserDto("Čvarkov", "Đorđe", "cvarkovibaba@pejicevisalasi.rs", "Divljibucak1!");
        User user = new User(1L, "Čvarkov", "Đorđe", "cvarkovibaba@pejicevisalasi.rs", "Divljibucak1!");
        UserDto userDTO = new UserDto(1L, "Čvarkov", "Đorđe", "cvarkovibaba@pejicevisalasi.rs");
        when(userMapper.mapCreateDtoToEntity(createUserDTO)).thenReturn(user);
        when(userMapper.mapEntityToDto(user)).thenReturn(userDTO);
        when(userRepository.save(user)).thenReturn(user);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(createUserDTO.getPassword())).thenReturn("Divljibucak1!");
        UserDto newlyCreatedUser = userService.createUser(createUserDTO);
        assertEquals(userDTO, newlyCreatedUser);
    }

    @Test
    public void shouldThrowBadRequestExceptionWhenCreateUserEmailIsNotUnique() {
        when(userMapper.mapCreateDtoToEntity(createUserDTO)).thenReturn(user);
        when(userMapper.mapEntityToDto(user)).thenReturn(userDTO);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        Exception exception = assertThrows(BadRequestException.class, () -> userService.createUser(createUserDTO));
        assertEquals("Email is already taken!", exception.getMessage());
    }

    @Test
    public void shouldReturnUpdatedUserWhenUpdateUserCalled() {
        when(userMapper.mapEntityToDto(user)).thenReturn(userDTO);
        doNothing().when(userMapper).updateUser(updateUserDto, user);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);
        UserDto updatedUser = userService.updateUser(updateUserDto, userDTO.getId());
        assertEquals(userDTO, updatedUser);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenUpdateUserIdNotFound() {
        when(userMapper.mapEntityToDto(user)).thenReturn(userDTO);
        doNothing().when(userMapper).updateUser(updateUserDto, user);
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        Exception exception = assertThrows(NotFoundException.class, () -> userService.updateUser(updateUserDto, userDTO.getId()));
        assertEquals("User not found!", exception.getMessage());
    }

    @Test
    public void shouldThrowBadRequestExceptionWhenUpdateUserEmailNotUnique() {
        when(userMapper.mapEntityToDto(user)).thenReturn(userDTO);
        doNothing().when(userMapper).updateUser(updateUserDto, user);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(new User(2L, "Čvarkov", "Đorđe", "cvarkovibaba@pejicevisalasi.rs", "divljibucak")));
        Exception exception = assertThrows(BadRequestException.class, () -> {
            updateUserDto.setEmail("cvarkovibaba@pejicevisalasi.rs");
            userService.updateUser(updateUserDto, user.getId());
        });
        assertEquals("Email is already taken!", exception.getMessage());
    }

    @Test
    public void shouldReturnUpdatedUserWhenUpdateUserEmailBelongsToThatUser() {
        when(userMapper.mapEntityToDto(user)).thenReturn(userDTO);
        doNothing().when(userMapper).updateUser(updateUserDto, user);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        UserDto updatedUserDto = userService.updateUser(updateUserDto, user.getId());
        assertEquals(userDTO, updatedUserDto);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenDeleteUserIdNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(NotFoundException.class, () -> userService.deleteUser(1L));
        assertEquals("User not found!", exception.getMessage());
    }
}
