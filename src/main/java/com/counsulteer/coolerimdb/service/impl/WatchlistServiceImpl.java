package com.counsulteer.coolerimdb.service.impl;

import com.counsulteer.coolerimdb.dto.watchlist.CreateWatchlistDto;
import com.counsulteer.coolerimdb.dto.watchlist.UpdateWatchlistDto;
import com.counsulteer.coolerimdb.dto.watchlist.WatchlistDto;
import com.counsulteer.coolerimdb.entity.Movie;
import com.counsulteer.coolerimdb.entity.Watchlist;
import com.counsulteer.coolerimdb.exception.BadRequestException;
import com.counsulteer.coolerimdb.exception.NotFoundException;
import com.counsulteer.coolerimdb.mapper.WatchlistMapper;
import com.counsulteer.coolerimdb.repository.MovieRepository;
import com.counsulteer.coolerimdb.repository.WatchlistRepository;
import com.counsulteer.coolerimdb.service.WatchlistService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class WatchlistServiceImpl implements WatchlistService {

    private static final String WATCHLIST_NOT_FOUND = "Watchlist not found!";
    private static final String MOVIE_NOT_FOUND = "Movie not found!";
    private static final String INVALID_PAGE_NUMBER = "Invalid page number!";
    private static final String INVALID_PAGE_SIZE = "Invalid page size!";

    private final WatchlistRepository watchlistRepository;
    private final WatchlistMapper watchlistMapper;
    private final MovieRepository movieRepository;

    @Override
    public WatchlistDto getWatchlist(Long id) {
        return watchlistMapper
                .mapEntityToDto(watchlistRepository
                        .findById(id)
                        .orElseThrow(() -> new NotFoundException(WATCHLIST_NOT_FOUND)));
    }

    @Override
    public List<WatchlistDto> getWatchlists() {
        return watchlistRepository
                .findAll().stream()
                .map(watchlistMapper::mapEntityToDto).collect(Collectors.toList());
    }

    @Override
    public WatchlistDto createWatchlist(CreateWatchlistDto newWatchlist) {
        return watchlistMapper
                .mapEntityToDto(watchlistRepository
                        .save(watchlistMapper.mapCreateDtoToEntity(newWatchlist)));
    }

    @Override
    public WatchlistDto updateWatchlist(UpdateWatchlistDto updateWatchlistDto, Long id) {
        Watchlist watchlist = watchlistRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(WATCHLIST_NOT_FOUND));

        watchlistMapper.updateWatchlist(updateWatchlistDto, watchlist);
        return watchlistMapper.mapEntityToDto(watchlistRepository.save(watchlist));
    }

    @Override
    public WatchlistDto addMovieToWatchlist(Long watchlistId, Long movieId) {
        Watchlist watchlist = watchlistRepository
                .findById(watchlistId)
                .orElseThrow(() -> new NotFoundException(WATCHLIST_NOT_FOUND));

        Movie movie = movieRepository
                .findById(movieId)
                .orElseThrow(() -> new NotFoundException(MOVIE_NOT_FOUND));

        watchlist.getMovies().add(movie);

        return watchlistMapper.mapEntityToDto(watchlistRepository.save(watchlist));
    }

    @Override
    public WatchlistDto removeMovieFromWatchlist(Long watchlistId, Long movieId) {
        Watchlist watchlist = watchlistRepository
                .findById(watchlistId)
                .orElseThrow(() -> new NotFoundException(WATCHLIST_NOT_FOUND));

        Movie movie = movieRepository
                .findById(movieId)
                .orElseThrow(() -> new NotFoundException(MOVIE_NOT_FOUND));

        watchlist.getMovies().remove(movie);

        return watchlistMapper.mapEntityToDto(watchlistRepository.save(watchlist));
    }

    @Override
    public Page<WatchlistDto> getWatchlistsByPage(Integer page, Integer size) {
        if (page < 0) throw new BadRequestException(INVALID_PAGE_NUMBER);
        if (size < 1) throw new BadRequestException(INVALID_PAGE_SIZE);

        return watchlistRepository
                .findAll(PageRequest.of(page, size))
                .map(watchlistMapper::mapEntityToDto);
    }

    @Override
    public void deleteWatchlist(Long id) {
        watchlistRepository.findById(id).ifPresentOrElse(watchlistRepository::delete, () -> {
            throw new NotFoundException(WATCHLIST_NOT_FOUND);
        });
    }

    @Override
    public void deleteMovieFromAllWatchlists(Long movieId) {
        watchlistRepository.findAll().forEach(watchlist -> removeMovieFromWatchlist(watchlist.getId() , movieId));
    }
}