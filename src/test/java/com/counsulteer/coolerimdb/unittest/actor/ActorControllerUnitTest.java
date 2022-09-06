package com.counsulteer.coolerimdb.unittest.actor;

import com.counsulteer.coolerimdb.controller.ActorController;
import com.counsulteer.coolerimdb.dto.actor.ActorDto;
import com.counsulteer.coolerimdb.dto.actor.BasicActorDto;
import com.counsulteer.coolerimdb.dto.movie.BasicMovieDto;
import com.counsulteer.coolerimdb.entity.Genre;
import com.counsulteer.coolerimdb.exception.BadRequestException;
import com.counsulteer.coolerimdb.exception.NotFoundException;
import com.counsulteer.coolerimdb.service.impl.ActorServiceImpl;
import com.counsulteer.coolerimdb.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.hamcrest.core.Is.is;
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

@WebMvcTest(controllers = ActorController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ActorControllerUnitTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserServiceImpl userService;
    @MockBean
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @MockBean
    private ActorServiceImpl actorService;
    private ActorDto actorDto;
    private BasicActorDto basicActorDto;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void beforeEach() {
        actorDto = new ActorDto(1L, "Keanu Reeves", LocalDate.now(), "abc", List.of(new BasicMovieDto(1L, "John Wick", "abc", "awesome movie", 0, 0, 0, "2014", LocalDate.now(), List.of(Genre.ACTION))));
        basicActorDto = new BasicActorDto(1L, "Keanu Reeves", LocalDate.now(), "abc");
    }

    @Test
    public void shouldReturnStatusOkWhenGetActorCalled() throws Exception {
        when(actorService.getActor(any())).thenReturn(actorDto);
        mockMvc.perform(get("/actors/{id}", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenGetActorIdIsNotFound() throws Exception {
        doThrow(new NotFoundException("Actor not found!")).when(actorService).getActor(any());
        mockMvc.perform(get("/actors/{id}", 1))
                .andDo(print())
                .andExpect(status().is(404))
                .andExpect(content().string("Actor not found!"));
    }

    @Test
    public void shouldReturnStatusOkWhenGetActorsCalled() throws Exception {
        when(actorService.getActors()).thenReturn(List.of(actorDto));
        mockMvc.perform(get("/actors"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void ShouldReturnStatusOkWhenCalled() throws Exception {
        when(actorService.getSortedActors(0, 1, Optional.empty(), Optional.empty())).thenReturn(new PageImpl<>(List.of(basicActorDto)));
        mockMvc.perform(get("/actors/sort/").queryParam("page", "0").queryParam("size", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    void shouldThrowBadRequestExceptionWhenInvalidPageNumberGiven() throws Exception {
        doThrow(new BadRequestException("Invalid page number!")).when(actorService).getSortedActors(any(), any(), any(), any());
        mockMvc.perform(get("/actors/sort/").queryParam("page", "-1").queryParam("size", "1"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid page number!"));
    }

    @Test
    void shouldThrowBadRequestExceptionWhenInvalidPageSizeGiven() throws Exception {
        doThrow(new BadRequestException("Invalid page size!")).when(actorService).getSortedActors(any(), any(), any(), any());
        mockMvc.perform(get("/actors/sort/").queryParam("page", "0").queryParam("size", "0"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid page size!"));
    }

    @Test
    void shouldReturnStatusOkWhenCreatingActor() throws Exception {
        objectMapper.findAndRegisterModules();
        when(actorService.createActor(any())).thenReturn(actorDto);
        mockMvc.perform(post("/actors").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(actorDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

    }

    @Test
    void shouldReturnStatusOkWhenUpdatingActor() throws Exception {
        objectMapper.findAndRegisterModules();
        when(actorService.updateActor(any(), any())).thenReturn(actorDto);
        mockMvc.perform(put("/actors/{id}", 1).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(actorDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUpdateActorNotFound() throws Exception {
        objectMapper.findAndRegisterModules();
        doThrow(new NotFoundException("Actor not found!")).when(actorService).updateActor(any(), any());
        mockMvc.perform(put("/actors/{id}", 1).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(actorDto)))
                .andExpect(status().is(404))
                .andExpect(jsonPath("$", is("Actor not found!")));
    }

    @Test
    void shouldReturnStatusOkWhenDeleteActorCalled() throws Exception {
        doNothing().when(actorService).deleteActor(any());
        mockMvc.perform(delete("/actors/{id}", 1))
                .andExpect(status().isOk());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenDeleteActorNotFound() throws Exception {
        doThrow(new NotFoundException("Actor not found!")).when(actorService).deleteActor(any());
        mockMvc.perform(delete("/actors/{id}", 1)).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$", is("Actor not found!")));
    }
}
