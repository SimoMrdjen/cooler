package com.counsulteer.coolerimdb.mapper;

import com.counsulteer.coolerimdb.dto.movie.BasicMovieDto;
import com.counsulteer.coolerimdb.dto.watchlist.CreateWatchlistDto;
import com.counsulteer.coolerimdb.dto.watchlist.UpdateWatchlistDto;
import com.counsulteer.coolerimdb.dto.watchlist.WatchlistDto;
import com.counsulteer.coolerimdb.entity.Movie;
import com.counsulteer.coolerimdb.entity.Watchlist;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@RequiredArgsConstructor
public class WatchlistMapper {

    private final MovieMapper movieMapper;

    public Watchlist mapCreateDtoToEntity(CreateWatchlistDto createWatchlistDto) {
        return new Watchlist(
                null,
                createWatchlistDto.getName(),
                mapBasicMovieDtoSetToEntitySet(createWatchlistDto.getMovies()));
    }

    public WatchlistDto mapEntityToDto(Watchlist watchlist) {
        return new WatchlistDto(
                watchlist.getId(),
                watchlist.getName(),
                mapEntityMovieSetToBasicMovieDtoSet(watchlist.getMovies()));
    }

    public void updateWatchlist(UpdateWatchlistDto updateWatchlistDto, Watchlist watchlist) {
        if (Objects.nonNull(updateWatchlistDto.getName()))
            watchlist.setName(updateWatchlistDto.getName());

        if (Objects.nonNull(updateWatchlistDto.getMovies()))
            watchlist.setMovies(
                    mapBasicMovieDtoSetToEntitySet(
                            updateWatchlistDto.getMovies()));
    }

    public Set<Movie> mapBasicMovieDtoSetToEntitySet(Set<BasicMovieDto> dtoSet) {
        Set<Movie> movies = new HashSet<>();
        dtoSet.forEach(movieDto -> movies.add(movieMapper.mapBasicDtoToEntity(movieDto)));

        return movies;
    }

    public Set<BasicMovieDto> mapEntityMovieSetToBasicMovieDtoSet(Set<Movie> movieSet) {
        Set<BasicMovieDto> movies = new HashSet<>();
        movieSet.forEach(movie -> movies.add(movieMapper.mapEntityToBasicDto(movie)));

        return movies;
    }
}
