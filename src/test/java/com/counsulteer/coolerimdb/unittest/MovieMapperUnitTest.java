package com.counsulteer.coolerimdb.unittest;

import com.counsulteer.coolerimdb.dto.movie.BasicMovieDto;
import com.counsulteer.coolerimdb.dto.movie.CreateMovieDto;
import com.counsulteer.coolerimdb.dto.movie.MovieDto;
import com.counsulteer.coolerimdb.dto.movie.UpdateMovieDto;
import com.counsulteer.coolerimdb.entity.Genre;
import com.counsulteer.coolerimdb.entity.Movie;
import com.counsulteer.coolerimdb.mapper.MovieMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class MovieMapperUnitTest {
    private final MovieMapper movieMapper = new MovieMapper();
    private CreateMovieDto createMovieDto;
    private UpdateMovieDto updateMovieDto;
    private Movie movie;
    private MovieDto movieDto;
    private BasicMovieDto basicMovieDto;

    @BeforeEach
    void beforeEach() {
        createMovieDto = new CreateMovieDto("John Wick", "abc", "description", "2014", List.of(Genre.ACTION), new ArrayList<>());
        updateMovieDto = new UpdateMovieDto("John Wick 2", "cba", "noitpircsed", "2017", List.of(Genre.ADVENTURE, Genre.ROMANCE), new ArrayList<>());
        movie = new Movie(1L, "John Wick", "abc", "description", "2014", List.of(Genre.ACTION), new ArrayList<>());
        movieDto = new MovieDto(1L, "John Wick", "abc", "description", 0, 0, 0, "2014", LocalDate.now(), List.of(Genre.ACTION), new ArrayList<>());
        basicMovieDto = new BasicMovieDto(1L, "John Wick", "abc", "description", 0, 0, 0, "2014", LocalDate.now(), List.of(Genre.ACTION));
    }

    @Test
    public void shouldMapEntityToDtoWhenCalled() {
        assertThat(movieMapper.mapEntityToDto(movie)).isEqualTo(movieDto);
    }

    @Test
    public void shouldMapCreateDtoToEntityWhenCalled() {
        movie.setId(null);
        assertThat(movieMapper.mapCreateDtoToEntity(createMovieDto)).isEqualTo(movie);
    }

    @Test
    public void shouldUpdateEntityWhenCalled() {
        movieMapper.updateEntity(movie, updateMovieDto);
        assertThat(movie).isEqualTo(new Movie(1L, "John Wick 2", "cba", "noitpircsed", "2017", List.of(Genre.ADVENTURE, Genre.ROMANCE), new ArrayList<>()));
    }

    @Test
    public void shouldMapEntityToBasicDtoWhenCalled() {
        assertThat(movieMapper.mapEntityToBasicDto(movie)).isEqualTo(basicMovieDto);
    }

    @Test
    public void shouldMapBasicDtoToEntityWhenCalled() {
        assertThat(movieMapper.mapBasicDtoToEntity(basicMovieDto)).isEqualTo(movie);
    }
}
