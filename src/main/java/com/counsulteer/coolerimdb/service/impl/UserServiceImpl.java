package com.counsulteer.coolerimdb.service.impl;

import com.counsulteer.coolerimdb.dto.user.CreateUserDto;
import com.counsulteer.coolerimdb.dto.user.UpdateUserDto;
import com.counsulteer.coolerimdb.dto.user.UserDto;
import com.counsulteer.coolerimdb.entity.User;
import com.counsulteer.coolerimdb.exception.BadRequestException;
import com.counsulteer.coolerimdb.exception.NotFoundException;
import com.counsulteer.coolerimdb.mapper.UserMapper;
import com.counsulteer.coolerimdb.repository.UserRepository;
import com.counsulteer.coolerimdb.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.RegexValidator;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String USER_NOT_FOUND = "User not found!";
    private static final String EMAIL_IS_ALREADY_TAKEN = "Email is already taken!";
    private static final String EMAIL_IS_NOT_VALID = "Email entered is not valid!";
    private static final String PASSWORD_IS_NOT_VALID = "Password entered is not valid!";

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto getUser(Long id) {
        return userMapper
                .mapEntityToDto(userRepository
                        .findById(id)
                        .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND)));
    }

    @Override
    public UserDto getUserByEmail(String email) {
        return userMapper
                .mapEntityToDto(userRepository
                .findByEmail(email)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND)));
    }

    @Override
    public List<UserDto> getUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::mapEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto createUser(CreateUserDto newUser) {
        if (userRepository.findByEmail(newUser.getEmail()).isPresent())
            throw new BadRequestException(EMAIL_IS_ALREADY_TAKEN);

        if (StringUtils.isNotEmpty(newUser.getEmail()) && !EmailValidator.getInstance().isValid(newUser.getEmail()))
            throw new BadRequestException(EMAIL_IS_NOT_VALID);

        RegexValidator regex = new RegexValidator("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>])(?=\\S+$).{8,}$");
        if (StringUtils.isNotEmpty(newUser.getPassword()) && !regex.isValid(newUser.getPassword()))
            throw new BadRequestException(PASSWORD_IS_NOT_VALID);

        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

        return userMapper.mapEntityToDto(userRepository.save(userMapper.mapCreateDtoToEntity(newUser)));
    }

    @Override
    public UserDto updateUser(UpdateUserDto updateUserDTO, Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

        Optional<User> userByEmailOptional = userRepository.findByEmail(updateUserDTO.getEmail());
        /*
          if the db contains a user with email from the given DTO parameter then that user has to have the same value as
          given id parameter, otherwise email is already taken and exception is thrown
        */
        if (userByEmailOptional.isPresent() && !userByEmailOptional.get().getId().equals(id))
            throw new BadRequestException(EMAIL_IS_ALREADY_TAKEN);

        userMapper.updateUser(updateUserDTO, user);
        return userMapper.mapEntityToDto(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.findById(id).ifPresentOrElse(userRepository::delete, () -> {
            throw new NotFoundException(USER_NOT_FOUND);
        });
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found!"));

        log.info("User found in the database: {}", email);

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole().name()));
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }

    @Override
    public UserDto getLoggedInUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String email;
        if (principal instanceof UserDetails) {
            email = ((UserDetails)principal).getUsername();
        } else {
            email = principal.toString();
        }

        return this.getUserByEmail(email);
    }

}
