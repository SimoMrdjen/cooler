package com.counsulteer.coolerimdb.unittest.movie;

import com.counsulteer.coolerimdb.controller.MovieController;
import com.counsulteer.coolerimdb.dto.actor.BasicActorDto;
import com.counsulteer.coolerimdb.dto.movie.BasicMovieDto;
import com.counsulteer.coolerimdb.dto.movie.MovieDto;
import com.counsulteer.coolerimdb.entity.Genre;
import com.counsulteer.coolerimdb.exception.BadRequestException;
import com.counsulteer.coolerimdb.exception.NotFoundException;
import com.counsulteer.coolerimdb.service.impl.MovieServiceImpl;
import com.counsulteer.coolerimdb.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MovieController.class)
@AutoConfigureMockMvc(addFilters = false)
public class MovieControllerUnitTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserServiceImpl userService;
    @MockBean
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @MockBean
    private MovieServiceImpl movieService;
    private MovieDto movieDto;
    private BasicMovieDto basicMovieDto;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void beforeEach() {
        movieDto = new MovieDto(1L, "John Wick", "abc", "awesome movie", 0, 0, 0, "2014", LocalDate.now(), List.of(Genre.ACTION), List.of(new BasicActorDto(1L, "Keanu Reeves", LocalDate.now(), "abc")));
        basicMovieDto = new BasicMovieDto(1L, "John Wick", "abc", "awesome movie", 0, 0, 0, "2014", LocalDate.now(), List.of(Genre.ACTION));
    }

    @Test
    public void shouldReturnStatusOkWhenGetMovieCalled() throws Exception {
        when(movieService.getMovie(any())).thenReturn(movieDto);
        mockMvc.perform(get("/movies/{id}", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Is.is(1)));
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenGetMovieIdIsNotFound() throws Exception {
        doThrow(new NotFoundException("Movie not found!")).when(movieService).getMovie(any());
        mockMvc.perform(get("/movies/{id}", 1))
                .andDo(print())
                .andExpect(status().is(404))
                .andExpect(content().string("Movie not found!"));
    }

    @Test
    public void shouldReturnStatusOkWhenGetMoviesCalled() throws Exception {
        when(movieService.getMovies()).thenReturn(List.of(movieDto));
        mockMvc.perform(get("/movies"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void shouldReturnStatusOkWhenCalled() throws Exception {
        when(movieService.getSortedMovies(0, 1, Optional.empty(), Optional.empty())).thenReturn(new PageImpl<>(List.of(basicMovieDto)));
        mockMvc.perform(get("/movies/sort/").queryParam("page", "0").queryParam("size", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    void shouldThrowBadRequestExceptionWhenInvalidPageNumberGiven() throws Exception {
        doThrow(new BadRequestException("Invalid page number!")).when(movieService).getSortedMovies(any(), any(), any(), any());
        mockMvc.perform(get("/movies/sort/").queryParam("page", "-1").queryParam("size", "1"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid page number!"));
    }

    @Test
    void shouldThrowBadRequestExceptionWhenInvalidPageSizeGiven() throws Exception {
        doThrow(new BadRequestException("Invalid page size!")).when(movieService).getSortedMovies(any(), any(), any(), any());
        mockMvc.perform(get("/movies/sort/").queryParam("page", "0").queryParam("size", "0"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid page size!"));
    }

    @Test
    void shouldReturnStatusOkWhenCreatingMovie() throws Exception {
        objectMapper.findAndRegisterModules();
        when(movieService.createMovie(any())).thenReturn(movieDto);
        mockMvc.perform(post("/movies").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(movieDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Is.is(1)));

    }

    @Test
    void shouldReturnStatusOkWhenUpdatingActor() throws Exception {
        objectMapper.findAndRegisterModules();
        when(movieService.updateMovie(any(), any())).thenReturn(movieDto);
        mockMvc.perform(put("/movies/{id}", 1).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(movieDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Is.is(1)));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUpdateMovieNotFound() throws Exception {
        objectMapper.findAndRegisterModules();
        doThrow(new NotFoundException("Movie not found!")).when(movieService).updateMovie(any(), any());
        mockMvc.perform(put("/movies/{id}", 1).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(movieDto)))
                .andExpect(status().is(404))
                .andExpect(jsonPath("$", Is.is("Movie not found!")));
    }

    @Test
    void shouldReturnStatusOkWhenDeleteMovieCalled() throws Exception {
        doNothing().when(movieService).deleteMovie(any());
        mockMvc.perform(delete("/movies/{id}", 1))
                .andExpect(status().isOk());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenDeleteMovieNotFound() throws Exception {
        doThrow(new NotFoundException("Movie not found!")).when(movieService).deleteMovie(any());
        mockMvc.perform(delete("/movies/{id}", 1)).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$", Is.is("Movie not found!")));
    }

    @Test
    public void shouldReturnMovieDtoWhenIncrementLikesIsCalled() throws Exception {
        objectMapper.findAndRegisterModules();
        when(movieService.incrementLikes(any())).thenReturn(movieDto);
        mockMvc.perform(put("/movies/{id}/like", 1)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is(movieDto.getTitle())))
                .andExpect(jsonPath("$.likes", is(0)));
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenIncrementLikesMovieIdNotFound() throws Exception {
        doThrow(new NotFoundException("Movie not found!")).when(movieService).incrementLikes(any());
        mockMvc.perform(put("/movies/{id}/like", 1)).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$", is("Movie not found!")));
    }

    @Test
    public void shouldReturnMovieDtoWhenIncrementDislikesIsCalled() throws Exception {
        when(movieService.incrementDislikes(any())).thenReturn(movieDto);
        mockMvc.perform(put("/movies/{id}/dislike", 1)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is(movieDto.getTitle())))
                .andExpect(jsonPath("$.likes", is(0)));
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenIncrementDislikesMovieIdNotFound() throws Exception {
        doThrow(new NotFoundException("Movie not found!")).when(movieService).incrementDislikes(any());
        mockMvc.perform(put("/movies/{id}/dislike", 1)).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$", is("Movie not found!")));
    }
}
