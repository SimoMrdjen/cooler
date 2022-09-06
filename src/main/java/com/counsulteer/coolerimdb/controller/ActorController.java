package com.counsulteer.coolerimdb.controller;

import com.counsulteer.coolerimdb.dto.actor.ActorDto;
import com.counsulteer.coolerimdb.dto.actor.BasicActorDto;
import com.counsulteer.coolerimdb.dto.actor.CreateActorDto;
import com.counsulteer.coolerimdb.dto.actor.UpdateActorDto;
import com.counsulteer.coolerimdb.dto.user.UserDto;
import com.counsulteer.coolerimdb.entity.SortActorsBy;
import com.counsulteer.coolerimdb.service.ActorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "actors")
@RequiredArgsConstructor
public class ActorController {
    private final ActorService actorService;

    @Operation(summary = "Get actor", description = "Returns the actor with the specified id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User is found.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ActorDto.class))}),
            @ApiResponse(responseCode = "404", description = "Actor is not found.",
                    content = @Content)})
    @GetMapping(value = "/{id}")
    ActorDto getActor(@PathVariable(name = "id") Long id) {
        return actorService.getActor(id);
    }

    @Operation(summary = "Get actors", description = "Returns a list of all existing actors.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Page returned.",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ActorDto.class)))})
    })
    @GetMapping
    List<ActorDto> getActors() {
        return actorService.getActors();
    }

    @Operation(summary = "Get sorted actors", description = "Returns a page of actors sorted by the specified criterion.")
    @GetMapping(value = "/sort")
    Page<BasicActorDto> getSortedActors(@RequestParam Integer page, @RequestParam Integer size, @RequestParam Optional<SortActorsBy> sortBy, @RequestParam Optional<Sort.Direction> order) {
        return actorService.getSortedActors(page, size, sortBy, order);
    }

    @Operation(summary = "Add actor", description = "Creates a new actor with the provided data and returns it.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Actor is added",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))})
    })
    @PostMapping
    ActorDto addActor(@RequestBody CreateActorDto createActorDto) {
        return actorService.createActor(createActorDto);
    }

    @Operation(summary = "Update actor", description = "Updates the actor with the specified id using the provided data and returns the updated actor.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Actor is updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))}),
            @ApiResponse(responseCode = "404", description = "Actor is not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))})
    })
    @PutMapping(value = "/{id}")
    ActorDto updateActor(@RequestBody UpdateActorDto updateActorDto, @PathVariable Long id) {
        return actorService.updateActor(updateActorDto, id);
    }

    @Operation(summary = "Delete actor", description = "Deletes the actor with the specified id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Actor is deleted",
                    content = {@Content}),
            @ApiResponse(responseCode = "404", description = "Actor is not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))})
    })
    @DeleteMapping(value = "/{id}")
    void deleteActor(@PathVariable Long id) {
        actorService.deleteActor(id);
    }
}
