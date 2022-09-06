package com.counsulteer.coolerimdb.integrationtest;

import com.counsulteer.coolerimdb.CoolerimdbApplication;
import com.counsulteer.coolerimdb.dto.watchlist.CreateWatchlistDto;
import com.counsulteer.coolerimdb.dto.watchlist.UpdateWatchlistDto;
import com.counsulteer.coolerimdb.entity.Watchlist;
import com.counsulteer.coolerimdb.repository.WatchlistRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.json.JSONArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashSet;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = CoolerimdbApplication.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(
        locations = "classpath:application.properties")
public class WatchlistControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private WatchlistRepository repository;

    private ObjectMapper ow;
    private UpdateWatchlistDto updateWatchlistDto;

    @BeforeEach
    public void setUp() {
        Watchlist watchlist1 = new Watchlist(1L, "test1", new HashSet<>());
        repository.save(watchlist1);
        Watchlist watchlist2 = new Watchlist(2L, "test2", new HashSet<>());
        repository.save(watchlist2);

        ow = new ObjectMapper();
        updateWatchlistDto = new UpdateWatchlistDto("update", new HashSet<>());
    }

    @Test
    public void shouldGetStatus200WhenUpdateWatchlist() throws Exception {
        mvc.perform( MockMvcRequestBuilders
                        .put("/watchlists/{id}",1L)
                        .content(ow.writeValueAsString(updateWatchlistDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("update"));
    }

    @Test
    public void shouldGetStatus200WhenGetWatchlists() throws Exception {
        MvcResult result = mvc.perform(get("/watchlists")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        int sizeOfArray = new JSONArray(result.getResponse().getContentAsString()).length();
        assertEquals(3, sizeOfArray);
    }

    @Test
    public void shouldGetStatus200whenGetWatchlist() throws Exception {
        MvcResult result = mvc.perform(get("/watchlists/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String name = JsonPath.read(result.getResponse().getContentAsString(), "name");
        assertEquals("test2", name);
    }

    @Test
    public void shouldGetStatus200whenAddWatchlist() throws Exception{
        CreateWatchlistDto createWatchlistDto = new CreateWatchlistDto("create", new HashSet<>());
        String json = ow.writeValueAsString(createWatchlistDto);

        MvcResult result = mvc.perform(post("/watchlists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String nameOfAddedWatchlist = JsonPath.read(result.getResponse().getContentAsString(), "name");
        assertEquals(nameOfAddedWatchlist, createWatchlistDto.getName());
    }

    @Test
    public void shouldGetStatus200whenDeleteWatchlistById() throws Exception {
        mvc.perform( MockMvcRequestBuilders.delete("/watchlists/{id}", 2) )
                .andExpect(status().isOk());
    }
}