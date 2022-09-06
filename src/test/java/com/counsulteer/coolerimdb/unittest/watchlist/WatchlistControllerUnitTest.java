package com.counsulteer.coolerimdb.unittest.watchlist;

import com.counsulteer.coolerimdb.controller.WatchlistController;
import com.counsulteer.coolerimdb.entity.Watchlist;
import com.counsulteer.coolerimdb.mapper.MovieMapper;
import com.counsulteer.coolerimdb.mapper.WatchlistMapper;
import com.counsulteer.coolerimdb.service.impl.UserServiceImpl;
import com.counsulteer.coolerimdb.service.impl.WatchlistServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = WatchlistController.class, useDefaultFilters = true)
@AutoConfigureMockMvc(addFilters = false)
public class WatchlistControllerUnitTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private WatchlistServiceImpl watchlistService;
    @MockBean
    private UserServiceImpl userService;
    @MockBean
    private BCryptPasswordEncoder passwordEncoder;
    @MockBean
    private MovieMapper movieMapper;

    private Watchlist watchlist;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        watchlist = new Watchlist(1L, "watchlist", new HashSet<>());

        objectMapper = new ObjectMapper();

        List<Watchlist> allWatchlists = List.of(watchlist);
        WatchlistMapper watchlistMapper = new WatchlistMapper(movieMapper);

        given(watchlistService.getWatchlists())
                .willReturn(allWatchlists
                        .stream()
                        .map(watchlistMapper::mapEntityToDto)
                        .collect(Collectors.toList()));
    }

    @Test
    public void shouldReturnJsonArrayWhenGetWatchlistsCalled() throws Exception {
        mvc.perform(get("/watchlists")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is(watchlist.getName())));
    }

    @Test
    public void shouldReturnStatusOkWhenCreatingWatchlist() throws Exception {
        String json = objectMapper.writeValueAsString(watchlist);

        mvc.perform(post("/watchlists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void shouldReturnHttpMessageNotReadableExceptionWhenGivenEmptyBody() throws Exception {
        mvc.perform(post("/watchlists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest())
                .andExpect(status().is(400))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof HttpMessageNotReadableException));
    }

    @Test
    public void shouldReturnStatusOkWhenUpdatingWatchlists() throws Exception {
        Watchlist watchlistSecond = new Watchlist(1L, "test2", new HashSet<>());

        String json = objectMapper.writeValueAsString(watchlistSecond);

        mvc.perform(put("/watchlists/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void shouldReturnStatusOkWhenDeletingWatchlists() throws Exception {
        mvc.perform(delete("/watchlists/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

}
