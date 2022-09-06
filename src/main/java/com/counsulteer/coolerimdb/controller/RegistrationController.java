package com.counsulteer.coolerimdb.controller;

import com.counsulteer.coolerimdb.dto.user.CreateUserDto;
import com.counsulteer.coolerimdb.dto.user.UserDto;
import com.counsulteer.coolerimdb.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/registration")
@AllArgsConstructor
@Slf4j
public class RegistrationController {

    private final UserService userService;

    @Operation(summary = "Add user", description = "Creates a new user with the provided data and returns it.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User is added",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))}),
            @ApiResponse(responseCode = "400", description = "Email is already taken",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))})
    })
    @PostMapping
    public UserDto addUser(@Validated @RequestBody CreateUserDto newUser) {
        log.info("Requested to add user with the following data: " + newUser.toString());
        return userService.createUser(newUser);
    }

}
