package com.counsulteer.coolerimdb.unittest.actor;

import com.counsulteer.coolerimdb.dto.actor.ActorDto;
import com.counsulteer.coolerimdb.dto.actor.BasicActorDto;
import com.counsulteer.coolerimdb.dto.actor.CreateActorDto;
import com.counsulteer.coolerimdb.dto.actor.UpdateActorDto;
import com.counsulteer.coolerimdb.dto.movie.BasicMovieDto;
import com.counsulteer.coolerimdb.entity.Actor;
import com.counsulteer.coolerimdb.entity.Genre;
import com.counsulteer.coolerimdb.entity.Movie;
import com.counsulteer.coolerimdb.entity.SortActorsBy;
import com.counsulteer.coolerimdb.exception.BadRequestException;
import com.counsulteer.coolerimdb.exception.NotFoundException;
import com.counsulteer.coolerimdb.mapper.ActorMapper;
import com.counsulteer.coolerimdb.mapper.MovieMapper;
import com.counsulteer.coolerimdb.repository.ActorRepository;
import com.counsulteer.coolerimdb.repository.MovieRepository;
import com.counsulteer.coolerimdb.service.impl.ActorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
public class ActorServiceUnitTest {
    @Mock
    private ActorRepository actorRepository;
    @Mock
    private MovieRepository movieRepository;
    @Mock
    private ActorMapper actorMapper;
    @Mock
    private MovieMapper movieMapper;
    @InjectMocks
    private ActorServiceImpl actorService;
    private Actor actor;
    private Movie movie, movie2;
    private ActorDto actorDto;
    private UpdateActorDto updateActorDto;
    private final List<BasicActorDto> content = Arrays.asList(
            new BasicActorDto(1L, "actor1", LocalDate.now(), "abc1"),
            new BasicActorDto(2L, "actor2", LocalDate.now(), "abc2"),
            new BasicActorDto(3L, "actor3", LocalDate.now(), "abc3")
    );
    private final List<Actor> actors = Arrays.asList(
            new Actor(1L, "actor1", LocalDate.now(), "abc1", new ArrayList<>()),
            new Actor(2L, "actor2", LocalDate.now(), "abc2", new ArrayList<>()),
            new Actor(3L, "actor3", LocalDate.now(), "abc3", new ArrayList<>())
    );

    @BeforeEach
    public void beforeEach() {
        actor = new Actor(1L, "Keanu Reeves", LocalDate.now(), "image", new ArrayList<>());
        movie = new Movie(1L, "John Wick", "image", "description", "2014", List.of(Genre.ACTION), new ArrayList<>());
        movie2 = new Movie(2L, "Matrix", "image", "description", "2000", List.of(Genre.ACTION), new ArrayList<>());
        movie.addActor(actor);
        actorDto = new ActorDto(actor.getId(), actor.getFullName(), actor.getBirthday(), actor.getImage(), new ArrayList<>());
        updateActorDto = new UpdateActorDto(actor.getFullName(), actor.getBirthday(), actor.getImage(), List.of(new BasicMovieDto(movie2.getId(), movie2.getTitle(), movie2.getImage(), movie2.getDescription(), 0, 0, 0, movie2.getYearOfRelease(), movie2.getDateOfCreation(), movie2.getGenres())));
    }

    @Test
    public void shouldReturnActorDtoWhenGetActorCalled() {
        when(actorRepository.findById(anyLong())).thenReturn(Optional.ofNullable(actor));
        actorDto.getMovies().add(new BasicMovieDto(movie.getId(), movie.getTitle(), movie.getImage(), movie.getDescription(), movie.getLikes(), movie.getDislikes(), movie.getRating(), movie.getYearOfRelease(), movie.getDateOfCreation(), movie.getGenres()));
        when(actorMapper.mapEntityToDto(any(Actor.class))).thenReturn(actorDto);
        when(movieMapper.mapEntityToBasicDto(any(Movie.class)))
                .thenAnswer(invocation -> {
                    Movie parameter = invocation.getArgument(0);
                    return new BasicMovieDto(parameter.getId(), parameter.getTitle(), movie.getImage(), movie.getDescription(), 0, 0, 0, movie.getYearOfRelease(), movie.getDateOfCreation(), movie.getGenres());
                });
        assertThat(actorService.getActor(1L)).isEqualTo(actorDto);

    }

    @Test
    public void shouldThrowNotFoundExceptionWhenActorIdNotFound() {
        when(actorRepository.findById(anyLong())).thenReturn(Optional.empty());
        Exception exception = assertThrows(NotFoundException.class, () -> actorService.getActor(1L));
        assertThat(exception.getMessage()).isEqualTo("Actor not found!");
    }

    @Test
    public void shouldReturnListOfActorDtoWhenGetActorsCalled() {
        when(actorRepository.findAll()).thenReturn(List.of(actor));
        when(actorMapper.mapEntityToDto(any(Actor.class))).thenReturn(actorDto);
        when(movieMapper.mapEntityToBasicDto(any(Movie.class)))
                .thenAnswer(invocation -> {
                    Movie parameter = invocation.getArgument(0);
                    return new BasicMovieDto(parameter.getId(), parameter.getTitle(), movie.getImage(), movie.getDescription(), 0, 0, 0, movie.getYearOfRelease(), movie.getDateOfCreation(), movie.getGenres());
                });
        assertThat(actorService.getActors()).isEqualTo(List.of(actorDto));
    }

    @MockitoSettings(strictness = Strictness.WARN)
    @Test
    public void shouldCreateActorWhenCreateActorCalled() {
        when(actorRepository.save(any(Actor.class))).thenReturn(actor);
        when(movieRepository.findById(anyLong())).thenReturn(Optional.of(movie));
        when(actorMapper.mapCreateDtoToEntity(any(CreateActorDto.class))).thenReturn(actor);
        when(actorMapper.mapEntityToDto(any(Actor.class))).thenReturn(actorDto);
        when(movieMapper.mapEntityToBasicDto(any(Movie.class)))
                .thenAnswer(invocation -> {
                    Movie parameter = invocation.getArgument(0);
                    return new BasicMovieDto(parameter.getId(), parameter.getTitle(), parameter.getImage(), parameter.getDescription(), 0, 0, 0, parameter.getYearOfRelease(), parameter.getDateOfCreation(), parameter.getGenres());
                });
        assertThat(actorService.createActor(new CreateActorDto(actor.getFullName(), actor.getBirthday(), actor.getImage(), List.of(new BasicMovieDto(movie.getId(), movie.getTitle(), movie.getImage(), movie.getDescription(), 0, 0, 0, movie.getYearOfRelease(), movie.getDateOfCreation(), movie.getGenres()))))).isEqualTo(actorDto);
    }

    @Test
    public void shouldUpdateActorWhenUpdateActorCalled() {
        actorDto.setMovies(List.of(new BasicMovieDto(movie2.getId(), movie2.getTitle(), movie2.getImage(), movie2.getDescription(), 0, 0, 0, movie2.getYearOfRelease(), movie2.getDateOfCreation(), movie2.getGenres())));
        when(actorRepository.findById(anyLong())).thenReturn(Optional.of(actor));
        when(movieRepository.findById(anyLong())).thenReturn(Optional.of(movie2));
        when(actorRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(actorMapper.mapEntityToDto(any())).thenAnswer(invocation -> {
            Actor actor = invocation.getArgument(0);
            return new ActorDto(actor.getId(), actor.getFullName(), actor.getBirthday(), actor.getImage(), new ArrayList<>());
        });
        doNothing().when(actorMapper).updateEntity(any(), any());
        when(movieMapper.mapEntityToBasicDto(any())).thenAnswer(invocation -> {
            Movie movie = invocation.getArgument(0);
            return new BasicMovieDto(movie.getId(), movie.getTitle(), movie.getImage(), movie.getDescription(), movie.getLikes(), movie.getDislikes(), movie.getRating(), movie.getYearOfRelease(), movie.getDateOfCreation(), movie.getGenres());
        });
        ActorDto result = actorService.updateActor(updateActorDto, 1L);
        assertThat(result).isEqualTo(actorDto);
        assertThat(result.getMovies().size()).isEqualTo(1);
        assertThat(result.getMovies().get(0).getId()).isEqualTo(2L);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenUpdateActorIdNotFound() {
        when(actorRepository.findById(any())).thenReturn(Optional.empty());
        Exception exception = assertThrows(NotFoundException.class, () -> actorService.updateActor(updateActorDto, 1L));
        assertThat(exception.getMessage()).isEqualTo("Actor not found!");
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenDeleteActorIdNotFound() {
        when(actorRepository.findById(any())).thenReturn(Optional.empty());
        Exception exception = assertThrows(NotFoundException.class, () -> actorService.deleteActor(1L));
        assertThat(exception.getMessage()).isEqualTo("Actor not found!");
    }

    @Test
    public void shouldReturnListOfSortedActors() {
        when(actorRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(actors));
        when(actorMapper.mapEntityToBasicDto(any())).thenAnswer((InvocationOnMock invocation) -> {
            Actor actor = (Actor) invocation.getArguments()[0];
            return new BasicActorDto(actor.getId(), actor.getFullName(), actor.getBirthday(), actor.getImage());
        });
        assertThat(actorService.getSortedActors(0, 5, Optional.empty(), Optional.empty()).getContent()).isEqualTo(content);
        assertThat(actorService.getSortedActors(0, 5, Optional.of(SortActorsBy.ID), Optional.of(Sort.Direction.ASC)).getContent()).isEqualTo(content);
        assertThat(actorService.getSortedActors(0, 5, Optional.of(SortActorsBy.NAME), Optional.of(Sort.Direction.ASC)).getContent()).isEqualTo(content);
        assertThat(actorService.getSortedActors(0, 5, Optional.of(SortActorsBy.BIRTH), Optional.of(Sort.Direction.ASC)).getContent()).isEqualTo(content);
        assertThat(actorService.getSortedActors(0, 5, Optional.of(SortActorsBy.ID), Optional.of(Sort.Direction.DESC)).getContent()).isEqualTo(content);
        assertThat(actorService.getSortedActors(0, 5, Optional.of(SortActorsBy.NAME), Optional.of(Sort.Direction.DESC)).getContent()).isEqualTo(content);
        assertThat(actorService.getSortedActors(0, 5, Optional.of(SortActorsBy.BIRTH), Optional.of(Sort.Direction.DESC)).getContent()).isEqualTo(content);
    }

    @Test
    public void shouldThrowBadRequestExceptionWhenInvalidPageNumberGiven() {
        Exception exception = assertThrows(BadRequestException.class, () -> actorService.getSortedActors(-1, 1, Optional.empty(), Optional.empty()));
        assertThat(exception.getMessage()).isEqualTo("Invalid page number!");
    }

    @Test
    public void shouldThrowBadRequestExceptionWhenInvalidPageSizeGiven() {
        Exception exception = assertThrows(BadRequestException.class, () -> actorService.getSortedActors(1, 0, Optional.empty(), Optional.empty()));
        assertThat(exception.getMessage()).isEqualTo("Invalid page size!");
    }
}
