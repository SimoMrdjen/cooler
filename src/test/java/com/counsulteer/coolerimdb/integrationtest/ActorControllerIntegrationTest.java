package com.counsulteer.coolerimdb.integrationtest;

import com.counsulteer.coolerimdb.CoolerimdbApplication;
import com.counsulteer.coolerimdb.dto.actor.CreateActorDto;
import com.counsulteer.coolerimdb.dto.actor.UpdateActorDto;
import com.counsulteer.coolerimdb.dto.movie.BasicMovieDto;
import com.counsulteer.coolerimdb.entity.Actor;
import com.counsulteer.coolerimdb.entity.Genre;
import com.counsulteer.coolerimdb.entity.Movie;
import com.counsulteer.coolerimdb.repository.ActorRepository;
import com.counsulteer.coolerimdb.repository.MovieRepository;
import com.counsulteer.coolerimdb.service.impl.ActorServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        classes = CoolerimdbApplication.class)
@AutoConfigureMockMvc(addFilters = false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ActorControllerIntegrationTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    private ActorRepository actorRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private ActorServiceImpl actorService;
    private List<Actor> actors;
    private List<Movie> movies;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void beforeEach() {
        actors = new ArrayList<>();
        movies = new ArrayList<>();
        actors.add(actorRepository.save(new Actor(null, "Keanu Reeves", LocalDate.now(), "abc", new ArrayList<>())));
        actors.add(actorRepository.save(new Actor(null, "Laurence Fishburne", LocalDate.now().minusDays(1L), "abc", new ArrayList<>())));
        movies.add(movieRepository.save(new Movie(null, "John Wick", "abc", "description", "2014", List.of(Genre.ACTION), List.of(actors.get(0)))));
        movies.add(movieRepository.save(new Movie(null, "Matrix", "abc", "description", "2014", List.of(Genre.ACTION), Arrays.asList(actors.get(0), actors.get(1)))));
    }

    @Test
    public void shouldReturnStatusOkWhenGetActorCalled() throws Exception {
        mockMvc.perform(get("/actors/{id}", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(actors.get(0).getId().intValue())))
                .andExpect(jsonPath("$.fullName", is(actors.get(0).getFullName())))
                .andExpect(jsonPath("$.movies", hasSize(2)))
                .andExpect(jsonPath("$.movies[0].title", is(movies.get(0).getTitle())))
                .andExpect(jsonPath("$.movies[1].title", is(movies.get(1).getTitle())));
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenGetActorIdNotFound() throws Exception {
        mockMvc.perform(get("/actors/{id}", 100))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$", is("Actor not found!")));
    }

    @Test
    public void shouldReturnStatusOkWhenGetActorsCalled() throws Exception {
        mockMvc.perform(get("/actors"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].fullName", is(actors.get(0).getFullName())))
                .andExpect(jsonPath("$[1].fullName", is(actors.get(1).getFullName())))
                .andExpect(jsonPath("$[0].movies", hasSize(2)))
                .andExpect(jsonPath("$[0].movies[0].title", is(movies.get(0).getTitle())))
                .andExpect(jsonPath("$[0].movies[1].title", is(movies.get(1).getTitle())))
                .andExpect(jsonPath("$[1].movies", hasSize(1)))
                .andExpect(jsonPath("$[1].movies[0].title", is(movies.get(1).getTitle())));
    }


    @Test
    void shouldReturnActorsSortedByIdInAscOrderWhenGetSortedActorsCalledWithOutSortByAndOrder() throws Exception {
        mockMvc.perform(get("/actors/sort/")
                        .queryParam("page", "0")
                        .queryParam("size", "20"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].fullName", is(actors.get(1).getFullName())))
                .andExpect(jsonPath("$.content[1].fullName", is(actors.get(0).getFullName())));
    }

    @Test
    void shouldReturnActorsSortedByIdInAscOrderWhenGetSortedActorsSortByIsIdAndOrderIsAsc() throws Exception {
        mockMvc.perform(get("/actors/sort/")
                        .queryParam("page", "0")
                        .queryParam("size", "20")
                        .queryParam("sortBy", "ID")
                        .queryParam("order", "ASC"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].fullName", is(actors.get(0).getFullName())))
                .andExpect(jsonPath("$.content[1].fullName", is(actors.get(1).getFullName())));
    }

    @Test
    void shouldReturnActorsSortedByIdInDescOrderWhenGetSortedActorsSortByIsIdAndOrderIsDesc() throws Exception {
        mockMvc.perform(get("/actors/sort/")
                        .queryParam("page", "0")
                        .queryParam("size", "20")
                        .queryParam("sortBy", "ID")
                        .queryParam("order", "DESC"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].fullName", is(actors.get(1).getFullName())))
                .andExpect(jsonPath("$.content[1].fullName", is(actors.get(0).getFullName())));
    }

    @Test
    void shouldReturnActorsSortedByNameInAscOrderWhenGetSortedActorsSortByIsNameAndOrderIsAsc() throws Exception {
        mockMvc.perform(get("/actors/sort/")
                        .queryParam("page", "0")
                        .queryParam("size", "20")
                        .queryParam("sortBy", "NAME")
                        .queryParam("order", "ASC"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].fullName", is(actors.get(0).getFullName())))
                .andExpect(jsonPath("$.content[1].fullName", is(actors.get(1).getFullName())));
    }

    @Test
    void shouldReturnActorsSortedByNameInDescOrderWhenGetSortedActorsSortByIsNameAndOrderIsDesc() throws Exception {
        mockMvc.perform(get("/actors/sort/")
                        .queryParam("page", "0")
                        .queryParam("size", "20")
                        .queryParam("sortBy", "NAME")
                        .queryParam("order", "DESC"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].fullName", is(actors.get(1).getFullName())))
                .andExpect(jsonPath("$.content[1].fullName", is(actors.get(0).getFullName())));
    }

    @Test
    void shouldReturnActorsSortedByBirthInAscOrderWhenGetSortedActorsSortByIsBirthAndOrderIsAsc() throws Exception {
        mockMvc.perform(get("/actors/sort/")
                        .queryParam("page", "0")
                        .queryParam("size", "20")
                        .queryParam("sortBy", "BIRTH")
                        .queryParam("order", "ASC"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].fullName", is(actors.get(1).getFullName())))
                .andExpect(jsonPath("$.content[1].fullName", is(actors.get(0).getFullName())));
    }

    @Test
    void shouldReturnActorsSortedByBirthInDescOrderWhenGetSortedActorsSortByIsBirthAndOrderIsDesc() throws Exception {
        mockMvc.perform(get("/actors/sort/")
                        .queryParam("page", "0")
                        .queryParam("size", "20")
                        .queryParam("sortBy", "BIRTH")
                        .queryParam("order", "DESC"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].fullName", is(actors.get(0).getFullName())))
                .andExpect(jsonPath("$.content[1].fullName", is(actors.get(1).getFullName())));
    }

    @Test
    void shouldThrowBadRequestExceptionWhenGetSortedActorsInvalidPageNumberGiven() throws Exception {
        mockMvc.perform(get("/actors/sort/").queryParam("page", "-1").queryParam("size", "1"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid page number!"));
    }

    @Test
    void shouldThrowBadRequestExceptionWhenGetSortedActorsInvalidPageSizeGiven() throws Exception {
        mockMvc.perform(get("/actors/sort/").queryParam("page", "0").queryParam("size", "0"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid page size!"));
    }

    @Test
    void shouldReturnStatusOkWhenCreatingActor() throws Exception {
        objectMapper.findAndRegisterModules();
        CreateActorDto createActorDto = new CreateActorDto("Laurence Fishburne", LocalDate.now(), "abc", List.of(new BasicMovieDto(1L, "Matrix", "abc", "description", 0, 0, 0, "2014", LocalDate.now(), List.of(Genre.ACTION))));
        mockMvc.perform(post("/actors").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(createActorDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName", is("Laurence Fishburne")))
                .andExpect(jsonPath("$.movies", hasSize(1)))
                .andExpect(jsonPath("$.movies[0].title", is("John Wick")));

    }

    @Test
    void shouldReturnStatusOkWhenUpdatingActor() throws Exception {
        objectMapper.findAndRegisterModules();
        UpdateActorDto updateActorDto = new UpdateActorDto("KeanuReeves", LocalDate.now(), "abc", List.of(new BasicMovieDto(1L, "Matrix", "abc", "description", 0, 0, 0, "2014", LocalDate.now(), List.of(Genre.ACTION))));
        mockMvc.perform(put("/actors/{id}", actors.get(0).getId().intValue()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateActorDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName", is("KeanuReeves")))
                .andExpect(jsonPath("$.movies", hasSize(1)))
                .andExpect(jsonPath("$.movies[0].title", is("John Wick")));

    }

    @Test
    void shouldThrowNotFoundExceptionWhenUpdateActorNotFound() throws Exception {
        objectMapper.findAndRegisterModules();
        mockMvc.perform(put("/actors/{id}", 100).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(new CreateActorDto("name", LocalDate.now(), "abc", null))))
                .andExpect(status().is(404))
                .andExpect(jsonPath("$", is("Actor not found!")));
    }

    @Test
    void shouldReturnStatusOkWhenDeleteActorCalled() throws Exception {
        mockMvc.perform(delete("/actors/{id}", 1))
                .andExpect(status().isOk());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenDeleteActorNotFound() throws Exception {
        mockMvc.perform(delete("/actors/{id}", 100)).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$", is("Actor not found!")));
    }
}
