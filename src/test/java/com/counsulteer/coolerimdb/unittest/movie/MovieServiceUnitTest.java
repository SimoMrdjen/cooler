package com.counsulteer.coolerimdb.unittest.movie;

import com.counsulteer.coolerimdb.dto.actor.BasicActorDto;
import com.counsulteer.coolerimdb.dto.movie.BasicMovieDto;
import com.counsulteer.coolerimdb.dto.movie.CreateMovieDto;
import com.counsulteer.coolerimdb.dto.movie.MovieDto;
import com.counsulteer.coolerimdb.dto.movie.UpdateMovieDto;
import com.counsulteer.coolerimdb.dto.user.UserDto;
import com.counsulteer.coolerimdb.entity.Actor;
import com.counsulteer.coolerimdb.entity.Genre;
import com.counsulteer.coolerimdb.entity.Movie;
import com.counsulteer.coolerimdb.entity.MovieRating;
import com.counsulteer.coolerimdb.entity.Rating;
import com.counsulteer.coolerimdb.entity.SortMoviesBy;
import com.counsulteer.coolerimdb.exception.BadRequestException;
import com.counsulteer.coolerimdb.exception.NotFoundException;
import com.counsulteer.coolerimdb.mapper.ActorMapper;
import com.counsulteer.coolerimdb.mapper.MovieMapper;
import com.counsulteer.coolerimdb.repository.ActorRepository;
import com.counsulteer.coolerimdb.repository.MovieRepository;
import com.counsulteer.coolerimdb.repository.RatingRepository;
import com.counsulteer.coolerimdb.service.MovieService;
import com.counsulteer.coolerimdb.service.WatchlistService;
import com.counsulteer.coolerimdb.service.impl.MovieServiceImpl;
import com.counsulteer.coolerimdb.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MovieServiceUnitTest {
    @Mock
    private ActorRepository actorRepository;
    @Mock
    private MovieRepository movieRepository;
    @Mock
    private MovieMapper movieMapper;
    @Mock
    private ActorMapper actorMapper;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private RatingRepository ratingRepository;
    @Mock
    private WatchlistService watchlistService;
    @InjectMocks
    private MovieServiceImpl movieService;
    private Actor actor, actor2;
    private Movie movie;
    private MovieDto movieDto;
    private UpdateMovieDto updateMovieDto;
    private UserDto userDto;
    private final List<BasicMovieDto> content = Arrays.asList(
            new BasicMovieDto(1L, "title1", "abc", "description1", 0, 0, 0, "2014", LocalDate.now(), List.of(Genre.ACTION)),
            new BasicMovieDto(2L, "title2", "abc", "description2", 0, 0, 0, "2015", LocalDate.now().minusDays(2L), List.of(Genre.ACTION)),
            new BasicMovieDto(3L, "title2", "abc", "description3", 0, 0, 0, "2016", LocalDate.now().minusDays(1L), List.of(Genre.ACTION))
    );
    private final List<Movie> movies = Arrays.asList(
            new Movie(1L, "title1", "abc", "description1", 0, 0, 0, "2014", LocalDate.now(), List.of(Genre.ACTION), new ArrayList<>()),
            new Movie(2L, "title2", "abc", "description2", 0, 0, 0, "2015", LocalDate.now().minusDays(2L), List.of(Genre.ACTION), new ArrayList<>()),
            new Movie(3L, "title2", "abc", "description3", 0, 0, 0, "2016", LocalDate.now().minusDays(1L), List.of(Genre.ACTION), new ArrayList<>())
    );

    @BeforeEach
    public void beforeEach() {
        movie = new Movie(1L, "John Wick", "abc", "nice movie", 0, 0, 0, "2014", LocalDate.now(), List.of(Genre.ACTION), new ArrayList<>());
        actor = new Actor(1L, "Keanu Reeves", LocalDate.now(), "image", new ArrayList<>());
        actor2 = new Actor(2L, "Laurence Fishburne", LocalDate.now(), "image", new ArrayList<>());
        movie.addActor(actor);
        movieDto = new MovieDto(1L, "John Wick", "abc", "nice movie", 0, 0, 0, "2014", LocalDate.now(), List.of(Genre.ACTION), new ArrayList<>());
        updateMovieDto = new UpdateMovieDto("John Wick", "abc", "description", "2014", List.of(Genre.ADVENTURE,Genre.SCIFI), List.of(new BasicActorDto(actor2.getId(), actor2.getFullName(), actor2.getBirthday(), actor2.getImage())));
        userDto = new UserDto(1L, "Rade", "Kornjača", "kornjaca.rade@gmail.com");
    }

    @Test
    public void shouldReturnMovieDtoWhenGetMovieCalled() {
        when(movieRepository.findById(anyLong())).thenReturn(Optional.empty());
        Exception exception = assertThrows(NotFoundException.class, () -> movieService.getMovie(1L));
        assertThat(exception.getMessage()).isEqualTo("Movie not found!");

    }

    @Test
    public void shouldThrowNotFoundExceptionWhenGetMovieIdNotFound() {
        when(movieRepository.findById(anyLong())).thenReturn(Optional.empty());
        Exception exception = assertThrows(NotFoundException.class, () -> movieService.getMovie(1L));
        assertThat(exception.getMessage()).isEqualTo("Movie not found!");
    }

    @Test
    public void shouldReturnListOfMovieDtoWhenGetMoviesCalled() {
        when(movieRepository.findAll()).thenReturn(List.of(movie));
        when(movieMapper.mapEntityToDto(any(Movie.class))).thenReturn(movieDto);
        when(actorMapper.mapEntityToBasicDto(any(Actor.class)))
                .thenAnswer(invocation -> {
                    Actor parameter = invocation.getArgument(0);
                    return new BasicActorDto(parameter.getId(), parameter.getFullName(), parameter.getBirthday(), parameter.getImage());
                });
        assertThat(movieService.getMovies()).isEqualTo(List.of(movieDto));
    }

    @MockitoSettings(strictness = Strictness.WARN)
    @Test
    public void shouldCreateMovieWhenCreateMovieCalled() {
        movie.setActors(new ArrayList<>());
        when(movieRepository.save(any(Movie.class))).thenReturn(movie);
        when(actorRepository.findById(anyLong())).thenReturn(Optional.of(actor));
        when(movieMapper.mapCreateDtoToEntity(any(CreateMovieDto.class))).thenReturn(movie);
        when(movieMapper.mapEntityToDto(any(Movie.class))).thenReturn(movieDto);
        when(actorMapper.mapEntityToBasicDto(any(Actor.class)))
                .thenAnswer(invocation -> {
                    Actor parameter = invocation.getArgument(0);
                    return new BasicActorDto(parameter.getId(), parameter.getFullName(), parameter.getBirthday(), parameter.getImage());
                });
        assertThat(movieService.createMovie(new CreateMovieDto(movie.getTitle(), movie.getImage(), movie.getDescription(), movie.getYearOfRelease(), movie.getGenres(), List.of(new BasicActorDto(actor.getId(), actor.getFullName(), actor.getBirthday(), actor.getImage()))))).isEqualTo(movieDto);
    }

    @Test
    public void shouldUpdateMovieWhenUpdateMovieCalled() {
        updateMovieDto.setActors(List.of(new BasicActorDto(actor2.getId(), actor2.getFullName(), actor2.getBirthday(), actor2.getImage())));
        movieDto.setActors(List.of(new BasicActorDto(actor2.getId(), actor2.getFullName(), actor2.getBirthday(), actor2.getImage())));
        when(movieRepository.findById(anyLong())).thenReturn(Optional.of(movie));
        when(actorRepository.findById(anyLong())).thenReturn(Optional.of(actor2));
        when(movieRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(movieMapper.mapEntityToDto(any())).thenAnswer(invocation -> {
            Movie movie = invocation.getArgument(0);
            return new MovieDto(movie.getId(), movie.getTitle(), movie.getImage(), movie.getDescription(), 0, 0, 0, movie.getYearOfRelease(), movie.getDateOfCreation(), movie.getGenres(), new ArrayList<>());
        });
        //doNothing().when(movieMapper).updateEntity(any(), any());
        when(actorMapper.mapEntityToBasicDto(any())).thenAnswer(invocation -> {
            Actor actor = invocation.getArgument(0);
            return new BasicActorDto(actor.getId(), actor.getFullName(), actor.getBirthday(), actor.getImage());
        });
        MovieDto result = movieService.updateMovie(updateMovieDto, 1L);
        assertThat(result).isEqualTo(movieDto);
        assertThat(result.getActors().size()).isEqualTo(1);
        assertThat(result.getActors().get(0).getId()).isEqualTo(2L);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenUpdateMovieIdNotFound() {
        when(movieRepository.findById(any())).thenReturn(Optional.empty());
        Exception exception = assertThrows(NotFoundException.class, () -> movieService.updateMovie(updateMovieDto, 1L));
        assertThat(exception.getMessage()).isEqualTo("Movie not found!");
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenDeleteMovieIdNotFound() {
        when(movieRepository.findById(any())).thenReturn(Optional.empty());
        Exception exception = assertThrows(NotFoundException.class, () -> movieService.deleteMovie(1L));
        assertThat(exception.getMessage()).isEqualTo("Movie not found!");
    }

    @Test
    public void shouldReturnListOfSortedMovies() {
        when(movieRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(movies));
        when(movieMapper.mapEntityToBasicDto(any())).thenAnswer((InvocationOnMock invocation) -> {
            Movie movie = (Movie) invocation.getArguments()[0];
            return new BasicMovieDto(movie.getId(),
                    movie.getTitle(),
                    movie.getImage(),
                    movie.getDescription(),
                    movie.getLikes(),
                    movie.getDislikes(),
                    movie.getRating(),
                    movie.getYearOfRelease(),
                    movie.getDateOfCreation(),
                    movie.getGenres());
        });
        assertThat(movieService.getSortedMovies(0, 5, Optional.empty(), Optional.empty()).getContent()).isEqualTo(content);
        assertThat(movieService.getSortedMovies(0, 5, Optional.of(SortMoviesBy.ID), Optional.of(Sort.Direction.ASC)).getContent()).isEqualTo(content);
        assertThat(movieService.getSortedMovies(0, 5, Optional.of(SortMoviesBy.TITLE), Optional.of(Sort.Direction.ASC)).getContent()).isEqualTo(content);
        assertThat(movieService.getSortedMovies(0, 5, Optional.of(SortMoviesBy.CREATION), Optional.of(Sort.Direction.ASC)).getContent()).isEqualTo(content);
        assertThat(movieService.getSortedMovies(0, 5, Optional.of(SortMoviesBy.RATING), Optional.of(Sort.Direction.ASC)).getContent()).isEqualTo(content);
        assertThat(movieService.getSortedMovies(0, 5, Optional.of(SortMoviesBy.ID), Optional.of(Sort.Direction.DESC)).getContent()).isEqualTo(content);
        assertThat(movieService.getSortedMovies(0, 5, Optional.of(SortMoviesBy.TITLE), Optional.of(Sort.Direction.DESC)).getContent()).isEqualTo(content);
        assertThat(movieService.getSortedMovies(0, 5, Optional.of(SortMoviesBy.CREATION), Optional.of(Sort.Direction.DESC)).getContent()).isEqualTo(content);
        assertThat(movieService.getSortedMovies(0, 5, Optional.of(SortMoviesBy.RATING), Optional.of(Sort.Direction.DESC)).getContent()).isEqualTo(content);
    }

    @Test
    public void shouldThrowBadRequestExceptionWhenInvalidPageNumberGiven() {
        Exception exception = assertThrows(BadRequestException.class, () -> movieService.getSortedMovies(-1, 1, Optional.empty(), Optional.empty()));
        assertThat(exception.getMessage()).isEqualTo("Invalid page number!");
    }

    @Test
    public void shouldThrowBadRequestExceptionWhenInvalidPageSizeGiven() {
        Exception exception = assertThrows(BadRequestException.class, () -> movieService.getSortedMovies(1, 0, Optional.empty(), Optional.empty()));
        assertThat(exception.getMessage()).isEqualTo("Invalid page size!");
    }


    @Test
    public void shouldReturnMovieDtoWhenIncrementDislikesIsCalledForNotRatedMovie() {
        when(movieRepository.findById(anyLong())).thenReturn(Optional.of(movie));
        when(movieRepository.save(any())).thenAnswer((InvocationOnMock invocation) -> invocation.getArguments()[0]);
        when(movieMapper.mapEntityToDto(any())).thenAnswer((InvocationOnMock invocation) -> {
            Movie movie = (Movie) invocation.getArguments()[0];
            return new MovieDto(movie.getId(),
                    movie.getTitle(),
                    movie.getImage(),
                    movie.getDescription(),
                    movie.getLikes(),
                    movie.getDislikes(),
                    movie.getRating(),
                    movie.getYearOfRelease(),
                    movie.getDateOfCreation(),
                    movie.getGenres(),
                    null);
        });
        when(actorMapper.mapEntityToBasicDto(any())).thenAnswer((InvocationOnMock invocation) -> {
            Actor actor = (Actor) invocation.getArguments()[0];
            return new BasicActorDto(actor.getId(), actor.getFullName(), actor.getBirthday(), actor.getImage());
        });
        when(userService.getLoggedInUser()).thenReturn(userDto);
        when(ratingRepository.findByUserIdAndMovieId(any(), any())).thenReturn(Optional.of(new MovieRating(userDto.getId(), Rating.NONE, movie.getId())));
        MovieDto result = movieService.incrementDislikes(1L);
        assertThat(result.getDislikes()).isEqualTo(1);
        assertThat(result.getRating()).isEqualTo(-1);
    }

    @Test
    public void shouldReturnMovieDtoWhenIncrementDislikesIsCalledForDislikedMovie() {
        movie.setDislikes(1);
        movie.setRating(-1);
        when(movieRepository.findById(anyLong())).thenReturn(Optional.of(movie));
        when(movieRepository.save(any())).thenAnswer((InvocationOnMock invocation) -> invocation.getArguments()[0]);
        when(movieMapper.mapEntityToDto(any())).thenAnswer((InvocationOnMock invocation) -> {
            Movie movie = (Movie) invocation.getArguments()[0];
            return new MovieDto(movie.getId(),
                    movie.getTitle(),
                    movie.getImage(),
                    movie.getDescription(),
                    movie.getLikes(),
                    movie.getDislikes(),
                    movie.getRating(),
                    movie.getYearOfRelease(),
                    movie.getDateOfCreation(),
                    movie.getGenres(),
                    null);
        });
        when(actorMapper.mapEntityToBasicDto(any())).thenAnswer((InvocationOnMock invocation) -> {
            Actor actor = (Actor) invocation.getArguments()[0];
            return new BasicActorDto(actor.getId(), actor.getFullName(), actor.getBirthday(), actor.getImage());
        });
        when(userService.getLoggedInUser()).thenReturn(userDto);
        when(ratingRepository.findByUserIdAndMovieId(any(), any())).thenReturn(Optional.of(new MovieRating(userDto.getId(), Rating.DISLIKED, movie.getId())));
        MovieDto result = movieService.incrementDislikes(1L);
        assertThat(result.getDislikes()).isEqualTo(0);
        assertThat(result.getRating()).isEqualTo(0);
    }

    @Test
    public void shouldReturnMovieDtoWhenIncrementDislikesIsCalledForLikedMovie() {
        movie.setLikes(1);
        movie.setRating(1);
        when(movieRepository.findById(anyLong())).thenReturn(Optional.of(movie));
        when(movieRepository.save(any())).thenAnswer((InvocationOnMock invocation) -> invocation.getArguments()[0]);
        when(movieMapper.mapEntityToDto(any())).thenAnswer((InvocationOnMock invocation) -> {
            Movie movie = (Movie) invocation.getArguments()[0];
            return new MovieDto(movie.getId(),
                    movie.getTitle(),
                    movie.getImage(),
                    movie.getDescription(),
                    movie.getLikes(),
                    movie.getDislikes(),
                    movie.getRating(),
                    movie.getYearOfRelease(),
                    movie.getDateOfCreation(),
                    movie.getGenres(),
                    null);
        });
        when(actorMapper.mapEntityToBasicDto(any())).thenAnswer((InvocationOnMock invocation) -> {
            Actor actor = (Actor) invocation.getArguments()[0];
            return new BasicActorDto(actor.getId(), actor.getFullName(), actor.getBirthday(), actor.getImage());
        });
        when(userService.getLoggedInUser()).thenReturn(userDto);
        when(ratingRepository.findByUserIdAndMovieId(any(), any())).thenReturn(Optional.of(new MovieRating(userDto.getId(), Rating.LIKED, movie.getId())));
        MovieDto result = movieService.incrementDislikes(1L);
        assertThat(result.getDislikes()).isEqualTo(1);
        assertThat(result.getRating()).isEqualTo(-1);
    }

    @Test
    public void shouldReturnMovieDtoWhenIncrementLikesIsCalledForNotRatedMovie() {
        when(movieRepository.findById(anyLong())).thenReturn(Optional.of(movie));
        when(movieRepository.save(any())).thenAnswer((InvocationOnMock invocation) -> invocation.getArguments()[0]);
        when(movieMapper.mapEntityToDto(any())).thenAnswer((InvocationOnMock invocation) -> {
            Movie movie = (Movie) invocation.getArguments()[0];
            return new MovieDto(movie.getId(),
                    movie.getTitle(),
                    movie.getImage(),
                    movie.getDescription(),
                    movie.getLikes(),
                    movie.getDislikes(),
                    movie.getRating(),
                    movie.getYearOfRelease(),
                    movie.getDateOfCreation(),
                    movie.getGenres(),
                    null);
        });
        when(actorMapper.mapEntityToBasicDto(any())).thenAnswer((InvocationOnMock invocation) -> {
            Actor actor = (Actor) invocation.getArguments()[0];
            return new BasicActorDto(actor.getId(), actor.getFullName(), actor.getBirthday(), actor.getImage());
        });
        when(userService.getLoggedInUser()).thenReturn(userDto);
        when(ratingRepository.findByUserIdAndMovieId(any(), any())).thenReturn(Optional.of(new MovieRating(userDto.getId(), Rating.NONE, movie.getId())));
        MovieDto result = movieService.incrementLikes(1L);
        assertThat(result.getLikes()).isEqualTo(1);
        assertThat(result.getRating()).isEqualTo(1);
    }

    @Test
    public void shouldReturnMovieDtoWhenIncrementLikesIsCalledForDislikedMovie() {
        movie.setDislikes(1);
        movie.setRating(-1);
        when(movieRepository.findById(anyLong())).thenReturn(Optional.of(movie));
        when(movieRepository.save(any())).thenAnswer((InvocationOnMock invocation) -> invocation.getArguments()[0]);
        when(movieMapper.mapEntityToDto(any())).thenAnswer((InvocationOnMock invocation) -> {
            Movie movie = (Movie) invocation.getArguments()[0];
            return new MovieDto(movie.getId(),
                    movie.getTitle(),
                    movie.getImage(),
                    movie.getDescription(),
                    movie.getLikes(),
                    movie.getDislikes(),
                    movie.getRating(),
                    movie.getYearOfRelease(),
                    movie.getDateOfCreation(),
                    movie.getGenres(),
                    null);
        });
        when(actorMapper.mapEntityToBasicDto(any())).thenAnswer((InvocationOnMock invocation) -> {
            Actor actor = (Actor) invocation.getArguments()[0];
            return new BasicActorDto(actor.getId(), actor.getFullName(), actor.getBirthday(), actor.getImage());
        });
        when(userService.getLoggedInUser()).thenReturn(userDto);
        when(ratingRepository.findByUserIdAndMovieId(any(), any())).thenReturn(Optional.of(new MovieRating(userDto.getId(), Rating.DISLIKED, movie.getId())));
        MovieDto result = movieService.incrementLikes(1L);
        assertThat(result.getLikes()).isEqualTo(1);
        assertThat(result.getRating()).isEqualTo(1);
    }

    @Test
    public void shouldReturnMovieDtoWhenIncrementLikesIsCalledForLikedMovie() {
        movie.setLikes(1);
        movie.setRating(1);
        when(movieRepository.findById(anyLong())).thenReturn(Optional.of(movie));
        when(movieRepository.save(any())).thenAnswer((InvocationOnMock invocation) -> invocation.getArguments()[0]);
        when(movieMapper.mapEntityToDto(any())).thenAnswer((InvocationOnMock invocation) -> {
            Movie movie = (Movie) invocation.getArguments()[0];
            return new MovieDto(movie.getId(),
                    movie.getTitle(),
                    movie.getImage(),
                    movie.getDescription(),
                    movie.getLikes(),
                    movie.getDislikes(),
                    movie.getRating(),
                    movie.getYearOfRelease(),
                    movie.getDateOfCreation(),
                    movie.getGenres(),
                    null);
        });
        when(actorMapper.mapEntityToBasicDto(any())).thenAnswer((InvocationOnMock invocation) -> {
            Actor actor = (Actor) invocation.getArguments()[0];
            return new BasicActorDto(actor.getId(), actor.getFullName(), actor.getBirthday(), actor.getImage());
        });
        when(userService.getLoggedInUser()).thenReturn(userDto);
        when(ratingRepository.findByUserIdAndMovieId(any(), any())).thenReturn(Optional.of(new MovieRating(userDto.getId(), Rating.LIKED, movie.getId())));
        MovieDto result = movieService.incrementLikes(1L);
        assertThat(result.getLikes()).isEqualTo(0);
        assertThat(result.getRating()).isEqualTo(0);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenIncrementDislikesMovieIdNotFound() {
        when(movieRepository.findById(anyLong())).thenReturn(Optional.empty());
        Exception exception = assertThrows(NotFoundException.class, () -> movieService.incrementDislikes(1L));
        assertThat(exception.getMessage()).isEqualTo("Movie not found!");
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenIncrementLikesMovieIdNotFound() {
        when(movieRepository.findById(anyLong())).thenReturn(Optional.empty());
        Exception exception = assertThrows(NotFoundException.class, () -> movieService.incrementLikes(1L));
        assertThat(exception.getMessage()).isEqualTo("Movie not found!");
    }
}








