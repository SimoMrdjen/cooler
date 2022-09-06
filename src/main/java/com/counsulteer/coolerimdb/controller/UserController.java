package com.counsulteer.coolerimdb.controller;

import com.counsulteer.coolerimdb.dto.user.UpdateUserDto;
import com.counsulteer.coolerimdb.dto.user.UserDto;
import com.counsulteer.coolerimdb.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get users", description = "Returns a list of all the existing users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List returned",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = UserDto.class))))})
    @GetMapping
    public List<UserDto> getUsers() {
        return userService.getUsers();
    }

    @Operation(summary = "Get user", description = "Returns the user with the specified id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User is found.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))}),
            @ApiResponse(responseCode = "404", description = "User is not found.",
                    content = @Content)})
    @GetMapping(value = "/{id}")
    public UserDto getUser(@PathVariable(name = "id") Long id) {
        log.info("Requested for user with id: " + id);
        return userService.getUser(id);
    }

    @Operation(summary = "Update user", description = "Updates the user with the specified id using the provided data and returns the updated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User is updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))}),
            @ApiResponse(responseCode = "400", description = "Email is already taken",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "404", description = "User is not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))})
    })
    @PutMapping(value = "/{id}")
    public UserDto updateUser(@RequestBody UpdateUserDto updateUserDto, @PathVariable Long id) {
        log.info("Requested to update user with id: " + id + " and new data is following: " + updateUserDto.toString());
        return userService.updateUser(updateUserDto, id);
    }

    @Operation(summary = "Delete user", description = "Deletes the user with the specified id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User is deleted",
                    content = {@Content}),
            @ApiResponse(responseCode = "404", description = "User is not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))})
    })
    @DeleteMapping(value = "/{id}")
    public void deleteUser(@PathVariable(name = "id") Long id) {
        userService.deleteUser(id);
    }

}
