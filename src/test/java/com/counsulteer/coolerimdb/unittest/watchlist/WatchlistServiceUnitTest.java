package com.counsulteer.coolerimdb.unittest.watchlist;

import com.counsulteer.coolerimdb.dto.watchlist.CreateWatchlistDto;
import com.counsulteer.coolerimdb.dto.watchlist.UpdateWatchlistDto;
import com.counsulteer.coolerimdb.dto.watchlist.WatchlistDto;
import com.counsulteer.coolerimdb.entity.Watchlist;
import com.counsulteer.coolerimdb.exception.NotFoundException;
import com.counsulteer.coolerimdb.mapper.WatchlistMapper;
import com.counsulteer.coolerimdb.repository.MovieRepository;
import com.counsulteer.coolerimdb.repository.WatchlistRepository;
import com.counsulteer.coolerimdb.service.WatchlistService;
import com.counsulteer.coolerimdb.service.impl.WatchlistServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WatchlistServiceUnitTest {

    private final WatchlistRepository watchlistRepository = Mockito.mock(WatchlistRepository.class);
    private final WatchlistMapper watchlistMapper = Mockito.mock(WatchlistMapper.class);
    private final MovieRepository movieRepository = Mockito.mock(MovieRepository.class);
    private final WatchlistService watchlistService = new WatchlistServiceImpl(watchlistRepository, watchlistMapper, movieRepository);

    private CreateWatchlistDto createWatchlistDto;
    private Watchlist watchlist1;
    private Watchlist watchlist2;
    private WatchlistDto watchlistDto1;
    private WatchlistDto watchlistDto2;
    private UpdateWatchlistDto updateWatchlistDto;

    @BeforeEach
    void setUp() {
        createWatchlistDto = new CreateWatchlistDto("create", new HashSet<>());
        watchlist1 = new Watchlist(1L, "watchlist1", new HashSet<>());
        watchlist2 = new Watchlist(2L, "watchlist2", new HashSet<>());
        watchlistDto1 = new WatchlistDto(1L, "watchlist1", new HashSet<>());
        watchlistDto2 = new WatchlistDto(2L, "watchlist2", new HashSet<>());
        updateWatchlistDto = new UpdateWatchlistDto("update", new HashSet<>());
    }

    @Test
    public void shouldReturnWatchlistWhenGetWatchlistCalled() {
        when(watchlistMapper.mapEntityToDto(watchlist1)).thenReturn(watchlistDto1);
        when(watchlistRepository.findById(watchlist1.getId())).thenReturn(Optional.of(watchlist1));
        WatchlistDto watchlistDto = watchlistService.getWatchlist(1L);
        assertEquals(watchlistDto1, watchlistDto);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenGetWatchlistIdNotFound() {
        when(watchlistRepository.findById(watchlist1.getId())).thenReturn(Optional.empty());
        Exception exception = assertThrows(NotFoundException.class, () -> watchlistService.getWatchlist(watchlist1.getId()));
        assertEquals("Watchlist not found!", exception.getMessage());
    }

    @Test
    public void shouldReturnWatchlistsWhenGetWatchlistsCalled() {
        List<Watchlist> watchlists = List.of(watchlist1, watchlist2);
        List<WatchlistDto> watchlistDtos = List.of(watchlistDto1, watchlistDto2);

        when(watchlistMapper.mapEntityToDto(watchlist1)).thenReturn(watchlistDto1);
        when(watchlistMapper.mapEntityToDto(watchlist2)).thenReturn(watchlistDto2);
        when(watchlistRepository.findAll()).thenReturn(watchlists);

        assertEquals(watchlistDtos, watchlistService.getWatchlists());
    }

    @Test
    public void shouldReturnCreatedWatchlistWhenCreateWatchlistCalled() {
        when(watchlistMapper.mapCreateDtoToEntity(createWatchlistDto)).thenReturn(watchlist1);
        when(watchlistMapper.mapEntityToDto(watchlist1)).thenReturn(watchlistDto1);
        when(watchlistRepository.save(watchlist1)).thenReturn(watchlist1);

        WatchlistDto newlyCreatedWatchlist = watchlistService.createWatchlist(createWatchlistDto);
        assertEquals(watchlistDto1, newlyCreatedWatchlist);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenUpdateWatchlistIdNotFound() {
        when(watchlistMapper.mapEntityToDto(watchlist1)).thenReturn(watchlistDto1);
        doNothing().when(watchlistMapper).updateWatchlist(updateWatchlistDto, watchlist1);

        when(watchlistRepository.findById(watchlist1.getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> watchlistService.updateWatchlist(updateWatchlistDto, watchlistDto1.getId()));
        assertEquals("Watchlist not found!", exception.getMessage());
    }

    @Test
    public void shouldReturnUpdatedWatchlistWhenUpdateWatchlistCalled() {
        when(watchlistMapper.mapEntityToDto(watchlist1)).thenReturn(watchlistDto1);
        doNothing().when(watchlistMapper).updateWatchlist(updateWatchlistDto, watchlist1);

        when(watchlistRepository.findById(watchlist1.getId())).thenReturn(Optional.of(watchlist1));
        when(watchlistRepository.save(watchlist1)).thenReturn(watchlist1);

        WatchlistDto updatedWatchlistDto = watchlistService.updateWatchlist(updateWatchlistDto, watchlist1.getId());
        assertEquals(watchlistDto1, updatedWatchlistDto);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenDeleteWatchlistIdNotFound() {
        when(watchlistRepository.findById(1L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(NotFoundException.class, () -> watchlistService.deleteWatchlist(1L));
        assertEquals("Watchlist not found!", exception.getMessage());
    }

}
