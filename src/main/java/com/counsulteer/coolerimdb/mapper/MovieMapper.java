package com.counsulteer.coolerimdb.mapper;

import com.counsulteer.coolerimdb.dto.movie.BasicMovieDto;
import com.counsulteer.coolerimdb.dto.movie.CreateMovieDto;
import com.counsulteer.coolerimdb.dto.movie.MovieDto;
import com.counsulteer.coolerimdb.dto.movie.UpdateMovieDto;
import com.counsulteer.coolerimdb.entity.Movie;

import java.util.ArrayList;
import java.util.Objects;

public class MovieMapper {

    public Movie mapCreateDtoToEntity(CreateMovieDto createMovieDto) {
        return new Movie(null, createMovieDto.getTitle(), createMovieDto.getImage(), createMovieDto.getDescription(), createMovieDto.getYearOfRelease(), createMovieDto.getGenres(), new ArrayList<>());
    }

    public MovieDto mapEntityToDto(Movie movie) {
        return new MovieDto(movie.getId(), movie.getTitle(), movie.getImage(), movie.getDescription(), movie.getLikes(), movie.getDislikes(), movie.getRating(), movie.getYearOfRelease(), movie.getDateOfCreation(), movie.getGenres(), new ArrayList<>());
    }

    public void updateEntity(Movie movie, UpdateMovieDto updateMovieDto) {
        if (Objects.nonNull(updateMovieDto.getTitle()))
            movie.setTitle(updateMovieDto.getTitle());

        if (Objects.nonNull(updateMovieDto.getImage()))
            movie.setImage(updateMovieDto.getImage());

        if (Objects.nonNull(updateMovieDto.getDescription()))
            movie.setDescription(updateMovieDto.getDescription());

        if (Objects.nonNull(updateMovieDto.getYearOfRelease()))
            movie.setYearOfRelease(updateMovieDto.getYearOfRelease());

        if (Objects.nonNull(updateMovieDto.getGenres()))
            movie.setGenres(updateMovieDto.getGenres());
    }

    public Movie mapBasicDtoToEntity(BasicMovieDto basicMovieDto) {
        return new Movie(basicMovieDto.getId(), basicMovieDto.getTitle(), basicMovieDto.getImage(), basicMovieDto.getDescription(), basicMovieDto.getLikes(), basicMovieDto.getDislikes(), basicMovieDto.getRating(), basicMovieDto.getYearOfRelease(), basicMovieDto.getDateOfCreation(), basicMovieDto.getGenres(), new ArrayList<>());
    }

    public BasicMovieDto mapEntityToBasicDto(Movie movie) {
        return new BasicMovieDto(movie.getId(), movie.getTitle(), movie.getImage(), movie.getDescription(), movie.getLikes(), movie.getDislikes(), movie.getRating(), movie.getYearOfRelease(), movie.getDateOfCreation(), movie.getGenres());
    }
}
