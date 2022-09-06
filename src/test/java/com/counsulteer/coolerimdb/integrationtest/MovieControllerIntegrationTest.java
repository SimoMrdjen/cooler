package com.counsulteer.coolerimdb.integrationtest;

import com.counsulteer.coolerimdb.CoolerimdbApplication;
import com.counsulteer.coolerimdb.dto.actor.BasicActorDto;
import com.counsulteer.coolerimdb.dto.movie.CreateMovieDto;
import com.counsulteer.coolerimdb.dto.movie.UpdateMovieDto;
import com.counsulteer.coolerimdb.dto.user.UserDto;
import com.counsulteer.coolerimdb.entity.Actor;
import com.counsulteer.coolerimdb.entity.Genre;
import com.counsulteer.coolerimdb.entity.Movie;
import com.counsulteer.coolerimdb.entity.MovieRating;
import com.counsulteer.coolerimdb.entity.Rating;
import com.counsulteer.coolerimdb.repository.ActorRepository;
import com.counsulteer.coolerimdb.repository.MovieRepository;
import com.counsulteer.coolerimdb.repository.RatingRepository;
import com.counsulteer.coolerimdb.service.impl.MovieServiceImpl;
import com.counsulteer.coolerimdb.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = CoolerimdbApplication.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(
        locations = "classpath:application.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MovieControllerIntegrationTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    private ActorRepository actorRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private MovieServiceImpl movieService;
    @MockBean
    private UserServiceImpl userService;
    @MockBean
    private RatingRepository ratingRepository;
    private List<Actor> actors;
    private List<Movie> movies;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserDto userDto = new UserDto(1L, "Rade", "Kornjača", "rade.kornjaca@gmail.com");

    @BeforeEach
    public void beforeEach() {
        actors = new ArrayList<>();
        movies = new ArrayList<>();
        actors.add(actorRepository.save(new Actor(null, "Keanu Reeves", LocalDate.now(), "abc", null)));
        actors.add(actorRepository.save(new Actor(null, "Laurence Fishburne", LocalDate.now().minusDays(1L), "abc", null)));
        movies.add(movieRepository.save(new Movie(null, "John Wick", "abc", "description", 1, 0, 1, "2014", LocalDate.now(), List.of(Genre.ACTION), List.of(actors.get(0)))));
        movies.add(movieRepository.save(new Movie(null, "Matrix", "abc", "description", 0, 0, 0, "2014", LocalDate.now().minusDays(1), List.of(Genre.ACTION), Arrays.asList(actors.get(0), actors.get(1)))));
    }

    @Test
    public void shouldReturnStatusOkWhenGetMovieCalled() throws Exception {
        mockMvc.perform(get("/movies/{id}", "2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(movies.get(1).getId().intValue())))
                .andExpect(jsonPath("$.title", is(movies.get(1).getTitle())))
                .andExpect(jsonPath("$.actors", hasSize(2)))
                .andExpect(jsonPath("$.actors[0].fullName", is(actors.get(0).getFullName())))
                .andExpect(jsonPath("$.actors[1].fullName", is(actors.get(1).getFullName())));
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenGetMovieIdNotFound() throws Exception {
        mockMvc.perform(get("/movies/{id}", 100))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$", is("Movie not found!")));
    }

    @Test
    public void shouldReturnStatusOkWhenGetMoviesCalled() throws Exception {
        mockMvc.perform(get("/movies"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is(movies.get(0).getTitle())))
                .andExpect(jsonPath("$[1].title", is(movies.get(1).getTitle())))
                .andExpect(jsonPath("$[0].actors", hasSize(1)))
                .andExpect(jsonPath("$[0].actors[0].fullName", is(actors.get(0).getFullName())))
                .andExpect(jsonPath("$[1].actors", hasSize(2)))
                .andExpect(jsonPath("$[1].actors[0].fullName", is(actors.get(0).getFullName())))
                .andExpect(jsonPath("$[1].actors[1].fullName", is(actors.get(1).getFullName())));
    }


    @Test
    void shouldReturnMoviesSortedByIdInAscOrderWhenGetSortedMoviesCalledWithoutSortByAndOrder() throws Exception {
        mockMvc.perform(get("/movies/sort/")
                        .queryParam("page", "0")
                        .queryParam("size", "20"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].title", is(movies.get(0).getTitle())))
                .andExpect(jsonPath("$.content[1].title", is(movies.get(1).getTitle())));
    }

    @Test
    void shouldReturnMoviesSortedByIdInAscOrderWhenGetSortedMoviesSortByIsIdAndOrderIsAsc() throws Exception {
        mockMvc.perform(get("/movies/sort/")
                        .queryParam("page", "0")
                        .queryParam("size", "20")
                        .queryParam("sortBy", "ID")
                        .queryParam("order", "ASC"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].title", is(movies.get(0).getTitle())))
                .andExpect(jsonPath("$.content[1].title", is(movies.get(1).getTitle())));
    }

    @Test
    void shouldReturnMoviesSortedByIdInDescOrderWhenGetSortedMoviesSortByIsIdAndOrderIsDesc() throws Exception {
        mockMvc.perform(get("/movies/sort/")
                        .queryParam("page", "0")
                        .queryParam("size", "20")
                        .queryParam("sortBy", "ID")
                        .queryParam("order", "DESC"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].title", is(movies.get(1).getTitle())))
                .andExpect(jsonPath("$.content[1].title", is(movies.get(0).getTitle())));
    }

    @Test
    void shouldReturnMoviesSortedByTitleInAscOrderWhenGetSortedMoviesSortByIsTitleAndOrderIsAsc() throws Exception {
        mockMvc.perform(get("/movies/sort/")
                        .queryParam("page", "0")
                        .queryParam("size", "20")
                        .queryParam("sortBy", "TITLE")
                        .queryParam("order", "ASC"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].title", is(movies.get(0).getTitle())))
                .andExpect(jsonPath("$.content[1].title", is(movies.get(1).getTitle())));
    }

    @Test
    void shouldReturnMoviesSortedByTitleInDescOrderWhenGetSortedMoviesSortByIsTitleAndOrderIsDesc() throws Exception {
        mockMvc.perform(get("/movies/sort/")
                        .queryParam("page", "0")
                        .queryParam("size", "20")
                        .queryParam("sortBy", "TITLE")
                        .queryParam("order", "DESC"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].title", is(movies.get(1).getTitle())))
                .andExpect(jsonPath("$.content[1].title", is(movies.get(0).getTitle())));
    }

    @Test
    void shouldReturnMoviesSortedByRatingInAscOrderWhenGetSortedMoviesSortByIsRatingAndOrderIsAsc() throws Exception {
        mockMvc.perform(get("/movies/sort/")
                        .queryParam("page", "0")
                        .queryParam("size", "20")
                        .queryParam("sortBy", "RATING")
                        .queryParam("order", "ASC"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].title", is(movies.get(1).getTitle())))
                .andExpect(jsonPath("$.content[1].title", is(movies.get(0).getTitle())));
    }

    @Test
    void shouldReturnMoviesSortedByRatingInDescOrderWhenGetSortedMoviesSortByIsRatingAndOrderIsDesc() throws Exception {
        mockMvc.perform(get("/movies/sort/")
                        .queryParam("page", "0")
                        .queryParam("size", "20")
                        .queryParam("sortBy", "RATING")
                        .queryParam("order", "DESC"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].title", is(movies.get(0).getTitle())))
                .andExpect(jsonPath("$.content[1].title", is(movies.get(1).getTitle())));
    }

    @Test
    void shouldReturnMoviesSortedByDateOfCreationInAscOrderWhenGetSortedMoviesSortByIsDateOfCreationAndOrderIsAsc() throws Exception {
        mockMvc.perform(get("/movies/sort/")
                        .queryParam("page", "0")
                        .queryParam("size", "20")
                        .queryParam("sortBy", "CREATION")
                        .queryParam("order", "ASC"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].title", is(movies.get(1).getTitle())))
                .andExpect(jsonPath("$.content[1].title", is(movies.get(0).getTitle())));
    }

    @Test
    void shouldReturnMoviesSortedByDateOfCreationInDescOrderWhenGetSortedMoviesSortByIsDateOfCreationAndOrderIsDesc() throws Exception {
        mockMvc.perform(get("/movies/sort/")
                        .queryParam("page", "0")
                        .queryParam("size", "20")
                        .queryParam("sortBy", "CREATION")
                        .queryParam("order", "DESC"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].title", is(movies.get(0).getTitle())))
                .andExpect(jsonPath("$.content[1].title", is(movies.get(1).getTitle())));
    }

    @Test
    void shouldThrowBadRequestExceptionWhenGetSortedMoviesInvalidPageNumberGiven() throws Exception {
        mockMvc.perform(get("/movies/sort/").queryParam("page", "-1").queryParam("size", "1"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid page number!"));
    }

    @Test
    void shouldThrowBadRequestExceptionWhenGetSortedMoviesInvalidPageSizeGiven() throws Exception {
        mockMvc.perform(get("/movies/sort/").queryParam("page", "0").queryParam("size", "0"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid page size!"));
    }

    @Test
    void shouldReturnStatusOkWhenCreatingMovie() throws Exception {
        objectMapper.findAndRegisterModules();
        CreateMovieDto createMovieDto = new CreateMovieDto("Matrix", "abc", "awesome movie", "description", List.of(Genre.ACTION), List.of(new BasicActorDto(1L, "Laurence Fishburne", LocalDate.now(), "abc")));
        mockMvc.perform(post("/movies").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(createMovieDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Matrix")))
                .andExpect(jsonPath("$.actors", hasSize(1)))
                .andExpect(jsonPath("$.actors[0].fullName", is("Keanu Reeves")))
                .andExpect(jsonPath("$.genres", hasSize(1)))
                .andExpect(jsonPath("$.genres", hasItem("ACTION")));

    }

    @Test
    void shouldReturnStatusOkWhenUpdatingMovie() throws Exception {
        objectMapper.findAndRegisterModules();
        UpdateMovieDto updateMovieDto = new UpdateMovieDto("Matrix", "abc", "awesome movie", "description", List.of(Genre.SCIFI, Genre.DRAMA, Genre.DOCUMENTARY), List.of(new BasicActorDto(1L, "Laurence Fishburne", LocalDate.now(), "abc")));
        mockMvc.perform(put("/movies/{id}", actors.get(0).getId().intValue()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateMovieDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Matrix")))
                .andExpect(jsonPath("$.actors", hasSize(1)))
                .andExpect(jsonPath("$.actors[0].fullName", is("Keanu Reeves")))
                .andExpect(jsonPath("$.genres", hasSize(3)))
                .andExpect(jsonPath("$.genres", hasItem("DRAMA")))
                .andExpect(jsonPath("$.genres", hasItem("SCIFI")))
                .andExpect(jsonPath("$.genres", hasItem("DOCUMENTARY")));

    }

    @Test
    void shouldThrowNotFoundExceptionWhenUpdateMovieNotFound() throws Exception {
        objectMapper.findAndRegisterModules();
        mockMvc.perform(put("/movies/{id}", 100).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(new CreateMovieDto("name", "abc", "awesome movie", "2014", List.of(Genre.ACTION), null))))
                .andExpect(status().is(404))
                .andExpect(jsonPath("$", is("Movie not found!")));
    }

    @Test
    void shouldReturnStatusOkWhenDeleteMovieCalled() throws Exception {
        mockMvc.perform(delete("/movies/{id}", 1))
                .andExpect(status().isOk());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenDeleteMovieNotFound() throws Exception {
        mockMvc.perform(delete("/movies/{id}", 100)).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$", is("Movie not found!")));
    }


    @Test
    public void shouldReturnMovieDtoWhenIncrementDislikesIsCalledForNotRatedMovie() throws Exception {
        when(userService.getLoggedInUser()).thenReturn(userDto);
        when(ratingRepository.findByUserIdAndMovieId(any(), any())).thenReturn(Optional.of(new MovieRating(userDto.getId(), Rating.NONE, movies.get(1).getId())));
        mockMvc.perform(put("/movies/{id}/dislike", 2)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dislikes", is(1)))
                .andExpect(jsonPath("$.likes", is(0)))
                .andExpect(jsonPath("$.rating", is(-1)))
                .andExpect(jsonPath("$.actors", hasSize(2)))
                .andExpect(jsonPath("$.genres", hasSize(1)))
                .andExpect(jsonPath("$.genres", hasItem("ACTION")));
    }

    @Test
    public void shouldReturnMovieDtoWhenIncrementDislikesIsCalledForDislikedMovie() throws Exception {
        movies.get(1).setRating(-1);
        movies.get(1).setDislikes(1);
        movieRepository.save(movies.get(1));
        when(userService.getLoggedInUser()).thenReturn(userDto);
        when(ratingRepository.findByUserIdAndMovieId(any(), any())).thenReturn(Optional.of(new MovieRating(userDto.getId(), Rating.DISLIKED, movies.get(1).getId())));
        mockMvc.perform(put("/movies/{id}/dislike", 2)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dislikes", is(0)))
                .andExpect(jsonPath("$.likes", is(0)))
                .andExpect(jsonPath("$.rating", is(0)))
                .andExpect(jsonPath("$.actors", hasSize(2)))
                .andExpect(jsonPath("$.genres", hasSize(1)))
                .andExpect(jsonPath("$.genres", hasItem("ACTION")));
    }

    @Test
    public void shouldReturnMovieDtoWhenIncrementDislikesIsCalledForLikedMovie() throws Exception {
        movies.get(1).setRating(1);
        movies.get(1).setLikes(1);
        movieRepository.save(movies.get(1));
        when(userService.getLoggedInUser()).thenReturn(userDto);
        when(ratingRepository.findByUserIdAndMovieId(any(), any())).thenReturn(Optional.of(new MovieRating(userDto.getId(), Rating.LIKED, movies.get(1).getId())));
        mockMvc.perform(put("/movies/{id}/dislike", 2)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dislikes", is(1)))
                .andExpect(jsonPath("$.likes", is(0)))
                .andExpect(jsonPath("$.rating", is(-1)))
                .andExpect(jsonPath("$.actors", hasSize(2)))
                .andExpect(jsonPath("$.genres", hasSize(1)))
                .andExpect(jsonPath("$.genres", hasItem("ACTION")));
    }

    @Test
    public void shouldReturnMovieDtoWhenIncrementLikesIsCalledForNotRatedMovie() throws Exception {
        when(userService.getLoggedInUser()).thenReturn(userDto);
        when(ratingRepository.findByUserIdAndMovieId(any(), any())).thenReturn(Optional.of(new MovieRating(userDto.getId(), Rating.NONE, movies.get(1).getId())));
        mockMvc.perform(put("/movies/{id}/like", 2)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dislikes", is(0)))
                .andExpect(jsonPath("$.likes", is(1)))
                .andExpect(jsonPath("$.rating", is(1)))
                .andExpect(jsonPath("$.actors", hasSize(2)))
                .andExpect(jsonPath("$.genres", hasSize(1)))
                .andExpect(jsonPath("$.genres", hasItem("ACTION")));
    }

    @Test
    public void shouldReturnMovieDtoWhenIncrementLikesIsCalledForDislikedMovie() throws Exception {
        movies.get(1).setRating(-1);
        movies.get(1).setDislikes(1);
        movieRepository.save(movies.get(1));
        when(userService.getLoggedInUser()).thenReturn(userDto);
        when(ratingRepository.findByUserIdAndMovieId(any(), any())).thenReturn(Optional.of(new MovieRating(userDto.getId(), Rating.DISLIKED, movies.get(1).getId())));
        mockMvc.perform(put("/movies/{id}/like", 2)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dislikes", is(0)))
                .andExpect(jsonPath("$.likes", is(1)))
                .andExpect(jsonPath("$.rating", is(1)))
                .andExpect(jsonPath("$.actors", hasSize(2)))
                .andExpect(jsonPath("$.genres", hasSize(1)))
                .andExpect(jsonPath("$.genres", hasItem("ACTION")));
    }

    @Test
    public void shouldReturnMovieDtoWhenIncrementLikesIsCalledForLikedMovie() throws Exception {
        movies.get(1).setRating(1);
        movies.get(1).setLikes(1);
        movieRepository.save(movies.get(1));
        when(userService.getLoggedInUser()).thenReturn(userDto);
        when(ratingRepository.findByUserIdAndMovieId(any(), any())).thenReturn(Optional.of(new MovieRating(userDto.getId(), Rating.LIKED, movies.get(1).getId())));
        mockMvc.perform(put("/movies/{id}/like", 2)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dislikes", is(0)))
                .andExpect(jsonPath("$.likes", is(0)))
                .andExpect(jsonPath("$.rating", is(0)))
                .andExpect(jsonPath("$.actors", hasSize(2)))
                .andExpect(jsonPath("$.genres", hasSize(1)))
                .andExpect(jsonPath("$.genres", hasItem("ACTION")));
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenIncrementDislikesMovieIdNotFound() throws Exception {
        mockMvc.perform(put("/movies/{id}/dislike", 100)).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$", is("Movie not found!")));
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenIncrementLikesMovieIdNotFound() throws Exception {
        mockMvc.perform(put("/movies/{id}/dislike", 100)).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$", is("Movie not found!")));
    }

}
