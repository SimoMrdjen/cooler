package com.counsulteer.coolerimdb.service;

import com.counsulteer.coolerimdb.dto.watchlist.CreateWatchlistDto;
import com.counsulteer.coolerimdb.dto.watchlist.UpdateWatchlistDto;
import com.counsulteer.coolerimdb.dto.watchlist.WatchlistDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface WatchlistService {

    WatchlistDto getWatchlist(Long id);

    List<WatchlistDto> getWatchlists();

    WatchlistDto createWatchlist(CreateWatchlistDto newWatchlist);

    WatchlistDto updateWatchlist(UpdateWatchlistDto updateWatchlistDto, Long id);

    WatchlistDto addMovieToWatchlist(Long watchlistId, Long movieId);

    WatchlistDto removeMovieFromWatchlist(Long watchlistId, Long movieId);

    Page<WatchlistDto> getWatchlistsByPage(Integer page, Integer size);

    void deleteWatchlist(Long id);

    void deleteMovieFromAllWatchlists(Long movieId);
}