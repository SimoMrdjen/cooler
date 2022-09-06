package com.counsulteer.coolerimdb.service;

import com.counsulteer.coolerimdb.dto.movie.BasicMovieDto;
import com.counsulteer.coolerimdb.dto.movie.CreateMovieDto;
import com.counsulteer.coolerimdb.dto.movie.MovieDto;
import com.counsulteer.coolerimdb.dto.movie.UpdateMovieDto;
import com.counsulteer.coolerimdb.entity.SortMoviesBy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface MovieService {
    MovieDto getMovie(Long id);

    List<MovieDto> getMovies();

    Page<BasicMovieDto> getSortedMovies(Integer page, Integer size, Optional<SortMoviesBy> sortMoviesBy, Optional<Sort.Direction> sortingOrder);

    MovieDto createMovie(CreateMovieDto newMovie);

    MovieDto updateMovie(UpdateMovieDto movie, Long id);

    MovieDto incrementLikes(Long id);

    MovieDto incrementDislikes(Long id);

    void deleteMovie(Long id);
}
