package com.counsulteer.coolerimdb.controller;

import com.counsulteer.coolerimdb.dto.watchlist.CreateWatchlistDto;
import com.counsulteer.coolerimdb.dto.watchlist.UpdateWatchlistDto;
import com.counsulteer.coolerimdb.dto.watchlist.WatchlistDto;
import com.counsulteer.coolerimdb.service.WatchlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/watchlists")
@Slf4j
public class WatchlistController {

    private final WatchlistService watchlistService;

    @Operation(summary = "Get watchlists", description = "Returns a list of all the existing watchlists.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List returned",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = WatchlistDto.class))))})
    @GetMapping
    public List<WatchlistDto> getWatchlists() {
        return watchlistService.getWatchlists();
    }

    @Operation(summary = "Get watchlist", description = "Returns the watchlist with the specified id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Watchlist is found.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = WatchlistDto.class))}),
            @ApiResponse(responseCode = "404", description = "Watchlist is not found.",
                    content = @Content)})
    @GetMapping(value = "/{id}")
    public WatchlistDto getWatchlist(@PathVariable(name = "id") Long id) {
        log.info("Requested for a watchlist with id: " + id);
        return watchlistService.getWatchlist(id);
    }

    @Operation(summary = "Add watchlist", description = "Creates a new watchlist with the provided data and returns it.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Watchlist is added",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = WatchlistDto.class))})
    })
    @PostMapping
    public WatchlistDto addWatchlist(@Validated @RequestBody CreateWatchlistDto newWatchlist) {
        log.info("Requested to add watchlist with the following data: " + newWatchlist.toString());
        return watchlistService.createWatchlist(newWatchlist);
    }

    @Operation(summary = "Update watchlist", description = "Updates the watchlist with the specified id using the provided data and returns the updated watchlist.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Watchlist is updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = WatchlistDto.class))}),
            @ApiResponse(responseCode = "404", description = "Watchlist is not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))})
    })
    @PutMapping(value = "/{id}")
    public WatchlistDto updateWatchlist(@RequestBody UpdateWatchlistDto updateWatchlistDto, @PathVariable Long id) {
        log.info("Requested to update watchlist with id: " + id + " and new data is following: " + updateWatchlistDto.toString());
        return watchlistService.updateWatchlist(updateWatchlistDto, id);
    }

    @Operation(summary = "Add movie to a watchlist", description = "Adds a movie with specified id to a watchlist with specified id and returns that watchlist.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movie added to the watchlist.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = WatchlistDto.class))}),
            @ApiResponse(responseCode = "404", description = "Watchlist is not found.",
                    content = @Content)})
    @PostMapping(value = "/{watchlistId}/addMovie/{movieId}")
    public WatchlistDto addMovieToWatchlist(@PathVariable(name = "watchlistId") Long watchlistId,
                                            @PathVariable(name = "movieId") Long movieId) {
        log.info("Requested to add a movie with id: " + movieId + " to a watchlist with id: " + watchlistId);
        return watchlistService.addMovieToWatchlist(watchlistId, movieId);
    }

    @Operation(summary = "Remove movie from a watchlist", description = "Removes a movie with specified id from a watchlist with specified id and returns that watchlist.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movie removed from the watchlist.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = WatchlistDto.class))}),
            @ApiResponse(responseCode = "404", description = "Watchlist is not found.",
                    content = @Content)})
    @PostMapping(value = "/{watchlistId}/removeMovie/{movieId}")
    public WatchlistDto removeMovieFromWatchlist(@PathVariable(name = "watchlistId") Long watchlistId,
                                            @PathVariable(name = "movieId") Long movieId) {
        log.info("Requested to remove a movie with id: " + movieId + " from a watchlist with id: " + watchlistId);
        return watchlistService.removeMovieFromWatchlist(watchlistId, movieId);
    }

    @Operation(summary = "Get pageable watchlists", description = "Returns a page of watchlists.")
    @GetMapping(value = "/page")
    public Page<WatchlistDto> getWatchlistsByPage(@RequestParam Integer page, @RequestParam Integer size) {
        log.info("Requested " + size + " watchlists from page: " + page);
        return watchlistService.getWatchlistsByPage(page, size);
    }

    @Operation(summary = "Delete watchlist", description = "Deletes the watchlist with the specified id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Watchlist is deleted",
                    content = {@Content}),
            @ApiResponse(responseCode = "404", description = "Watchlist is not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))})
    })
    @DeleteMapping(value = "/{id}")
    public void deleteWatchlist(@PathVariable(name = "id") Long id) {
        watchlistService.deleteWatchlist(id);
    }
}
