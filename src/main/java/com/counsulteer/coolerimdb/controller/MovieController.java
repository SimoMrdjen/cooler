package com.counsulteer.coolerimdb.controller;

import com.counsulteer.coolerimdb.dto.movie.BasicMovieDto;
import com.counsulteer.coolerimdb.dto.movie.CreateMovieDto;
import com.counsulteer.coolerimdb.dto.movie.MovieDto;
import com.counsulteer.coolerimdb.dto.movie.UpdateMovieDto;
import com.counsulteer.coolerimdb.entity.SortMoviesBy;
import com.counsulteer.coolerimdb.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
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
@RequestMapping(value = "/movies")
@AllArgsConstructor
@Slf4j
public class MovieController {

    private final MovieService movieService;

    @Operation(summary = "Get movies", description = "Returns a list of all the existing movies.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List returned",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = MovieDto.class))))})
    @GetMapping
    public List<MovieDto> getMovies() {
        return movieService.getMovies();
    }

    @GetMapping(value = "/sort")
    public Page<BasicMovieDto> getSortedMovies(@RequestParam Integer page, @RequestParam Integer size, @RequestParam Optional<SortMoviesBy> sortBy, @RequestParam Optional<Sort.Direction> order) {
        return movieService.getSortedMovies(page, size, sortBy, order);
    }

    @Operation(summary = "Get movie", description = "Returns the movie with the specified id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movie is found.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MovieDto.class))}),
            @ApiResponse(responseCode = "404", description = "Movie is not found.",
                    content = @Content)})
    @GetMapping(value = "/{id}")
    public MovieDto getMovie(@PathVariable(name = "id") Long id) {
        log.info("Requested for movie with id: " + id);
        return movieService.getMovie(id);
    }

    @Operation(summary = "Add movie", description = "Creates a new movie with the provided data and returns it.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movie is added",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MovieDto.class))})
    })
    @PostMapping
    public MovieDto addMovie(@Validated @RequestBody CreateMovieDto newMovie) {
        log.info("Requested to add movie with the following data: " + newMovie.toString());
        return movieService.createMovie(newMovie);
    }

    @Operation(summary = "Update movie", description = "Updates the movie with the specified id using the provided data and returns the updated movie.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movie is updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MovieDto.class))}),
            @ApiResponse(responseCode = "404", description = "Movie is not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))})
    })
    @PutMapping(value = "/{id}")
    public MovieDto updateMovie(@RequestBody UpdateMovieDto updateMovieDto, @PathVariable Long id) {
        log.info("Requested to update movie with id: " + id + " and new data is following: " + updateMovieDto.toString());
        return movieService.updateMovie(updateMovieDto, id);
    }

    @Operation(summary = "Add a like to a movie", description = "If the logged-in user hasn't already liked, increment specified movie's likes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Added a like to the movie",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MovieDto.class))}),
            @ApiResponse(responseCode = "404", description = "Movie is not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))})
    })
    @PutMapping(value = "/{id}/like")
    public MovieDto incrementLikes(@PathVariable Long id) {
        log.info("Requested to add a like to a movie with id: " + id);
        return movieService.incrementLikes(id);
    }

    @Operation(summary = "Add a dislike to a movie", description = "If the logged-in user hasn't already disliked, increment specified movie's dislikes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Added a dislike to the movie",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MovieDto.class))}),
            @ApiResponse(responseCode = "404", description = "Movie is not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))})
    })
    @PutMapping(value = "/{id}/dislike")
    public MovieDto incrementDislikes(@PathVariable Long id) {
        log.info("Requested to add a dislike to a movie with id: " + id);
        return movieService.incrementDislikes(id);
    }

    @Operation(summary = "Delete movie", description = "Deletes the movie with the specified id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movie is deleted",
                    content = {@Content}),
            @ApiResponse(responseCode = "404", description = "Movie is not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))})
    })
    @DeleteMapping(value = "/{id}")
    public void deleteMovie(@PathVariable(name = "id") Long id) {
        movieService.deleteMovie(id);
    }
}
