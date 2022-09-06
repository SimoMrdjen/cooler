package com.counsulteer.coolerimdb.service.impl;

import com.counsulteer.coolerimdb.dto.movie.BasicMovieDto;
import com.counsulteer.coolerimdb.dto.movie.CreateMovieDto;
import com.counsulteer.coolerimdb.dto.movie.MovieDto;
import com.counsulteer.coolerimdb.dto.movie.UpdateMovieDto;
import com.counsulteer.coolerimdb.dto.user.UserDto;
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
import com.counsulteer.coolerimdb.service.UserService;
import com.counsulteer.coolerimdb.service.WatchlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private static final String MOVIE_NOT_FOUND = "Movie not found!";
    private static final String DESCRIPTION_TOO_LONG = "The description you have entered is too long!";
    private static final String ACTOR_NOT_FOUND = "Actor not found!";
    private static final String MOVIE_RATING_NOT_FOUND = "Movie rating not found!";
    private static final String INVALID_PAGE_NUMBER = "Invalid page number!";
    private static final String INVALID_PAGE_SIZE = "Invalid page size!";
    private final MovieRepository movieRepository;
    private final ActorRepository actorRepository;
    private final MovieMapper movieMapper;
    private final UserService userService;
    private final RatingRepository ratingRepository;
    private final WatchlistService watchlistService;
    private final ActorMapper actorMapper;

    @Override
    public MovieDto getMovie(Long id) {
        Movie movie = movieRepository.findById(id).orElseThrow(() -> new NotFoundException(MOVIE_NOT_FOUND));
        MovieDto movieDto = movieMapper.mapEntityToDto(movie);
        movieDto.setActors(movie.getActors().stream().map(actorMapper::mapEntityToBasicDto).collect(Collectors.toList()));
        return movieDto;
    }

    @Override
    public List<MovieDto> getMovies() {
        return movieRepository.findAll().stream().map(movie -> {
            MovieDto movieDto = movieMapper.mapEntityToDto(movie);
            movieDto.setActors(movie.getActors().stream().map(actorMapper::mapEntityToBasicDto).collect(Collectors.toList()));
            return movieDto;
        }).collect(Collectors.toList());
    }

    @Override
    public Page<BasicMovieDto> getSortedMovies(Integer page, Integer size, Optional<SortMoviesBy> sortBy, Optional<Sort.Direction> order) {
        if (page < 0) throw new BadRequestException(INVALID_PAGE_NUMBER);
        if (size < 1) throw new BadRequestException(INVALID_PAGE_SIZE);
        AtomicReference<Sort.Direction> orderValue = new AtomicReference<>(Sort.Direction.ASC);
        AtomicReference<SortMoviesBy> sortByValue = new AtomicReference<>(SortMoviesBy.ID);
        order.ifPresent(orderValue::set);
        sortBy.ifPresent(sortByValue::set);
        return movieRepository.findAll(PageRequest.of(page, size, orderValue.get(), sortByValue.get().getValue())).map(movieMapper::mapEntityToBasicDto);
    }

    @Override
    @Transactional
    public MovieDto createMovie(CreateMovieDto createMovieDto) {
        if (createMovieDto.getDescription().length() > 500) {
            throw new BadRequestException(DESCRIPTION_TOO_LONG);
        }
        Movie movie = movieMapper.mapCreateDtoToEntity(createMovieDto);
        Movie finalMovie = movie;
        createMovieDto.getActors().forEach(actor -> finalMovie.addActor(actorRepository.findById(actor.getId()).orElseThrow(() -> new NotFoundException(ACTOR_NOT_FOUND))));
        movie = movieRepository.save(finalMovie);
        MovieDto movieDto = movieMapper.mapEntityToDto(movie);
        movieDto.setActors(movie.getActors().stream().map(actorMapper::mapEntityToBasicDto).collect(Collectors.toList()));
        return movieDto;

    }

    @Override
    @Transactional
    public MovieDto updateMovie(UpdateMovieDto updateMovieDto, Long id) {
        Movie movie = movieRepository.findById(id).orElseThrow(() -> new NotFoundException(MOVIE_NOT_FOUND));
        movieMapper.updateEntity(movie, updateMovieDto);
        Movie finalMovie = movie;
        if (Objects.nonNull(updateMovieDto.getActors())) {
            updateMovieDto.getActors().forEach(actor -> {
                if (finalMovie.getActors().stream().noneMatch(a -> actor.getId().equals(a.getId()))) {
                    finalMovie.addActor(actorRepository.findById(actor.getId()).orElseThrow(() -> new NotFoundException(ACTOR_NOT_FOUND)));
                }
            });
            int i;
            for (i = 0; i < finalMovie.getActors().size(); i++) {
                int x = i;
                if (updateMovieDto.getActors().stream().noneMatch(m -> (m.getId().equals(finalMovie.getActors().get(x).getId())))) {
                    finalMovie.deleteActor(finalMovie.getActors().get(x));
                }
            }
        }
        movie = movieRepository.save(finalMovie);
        MovieDto movieDto = movieMapper.mapEntityToDto(movie);
        movieDto.setActors(movie.getActors().stream().map(actorMapper::mapEntityToBasicDto).collect(Collectors.toList()));
        return movieDto;
    }

    @Override
    public MovieDto incrementLikes(Long id) {
        Movie movie = movieRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(MOVIE_NOT_FOUND));

        switch (usersCurrentRating(id)) {
            case NONE:
                movie.incrementLikes();
                movie.updateRating();
                updateUserRating(Rating.LIKED, id);
                break;
            case DISLIKED:
                movie.incrementLikes();
                movie.decrementDislikes();
                movie.updateRating();
                updateUserRating(Rating.LIKED, id);
                break;
            case LIKED:
                movie.decrementLikes();
                movie.updateRating();
                updateUserRating(Rating.NONE, id);
        }

        movie = movieRepository.save(movie);
        MovieDto movieDto = movieMapper.mapEntityToDto(movie);
        movieDto.setActors(movie.getActors().stream().map(actorMapper::mapEntityToBasicDto).collect(Collectors.toList()));
        return movieDto;
    }

    @Override
    public MovieDto incrementDislikes(Long id) {
        Movie movie = movieRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(MOVIE_NOT_FOUND));

        switch (usersCurrentRating(id)) {
            case NONE:
                movie.incrementDislikes();
                movie.updateRating();
                updateUserRating(Rating.DISLIKED, id);
                break;
            case LIKED:
                movie.incrementDislikes();
                movie.decrementLikes();
                movie.updateRating();
                updateUserRating(Rating.DISLIKED, id);
                break;
            case DISLIKED:
                movie.decrementDislikes();
                movie.updateRating();
                updateUserRating(Rating.NONE, id);
        }

        movie = movieRepository.save(movie);
        MovieDto movieDto = movieMapper.mapEntityToDto(movie);
        movieDto.setActors(movie.getActors().stream().map(actorMapper::mapEntityToBasicDto).collect(Collectors.toList()));
        return movieDto;
    }

    @Override
    public void deleteMovie(Long id) {
        movieRepository.findById(id).ifPresentOrElse((movie) -> {
            while (movie.getActors().size() != 0) movie.getActors().get(0).deleteMovie(movie);
            watchlistService.deleteMovieFromAllWatchlists(id);
            movieRepository.deleteById(id);
        }, () -> {
            throw new NotFoundException(MOVIE_NOT_FOUND);
        });
    }

    public Rating usersCurrentRating(Long movieId) {
        UserDto user = userService.getLoggedInUser();

        MovieRating movieRating = ratingRepository
                .findByUserIdAndMovieId(user.getId(), movieId)
                .orElse(new MovieRating(user.getId(), Rating.NONE, movieId));

        if (Rating.NONE.equals(movieRating.getRating()))
            ratingRepository.save(movieRating);

        return movieRating.getRating();
    }

    public void updateUserRating(Rating rating, Long movieId) {
        UserDto user = userService.getLoggedInUser();

        MovieRating existingRating = ratingRepository
                .findByUserIdAndMovieId(user.getId(), movieId)
                .orElseThrow(() -> new NotFoundException(MOVIE_RATING_NOT_FOUND));

        existingRating.setRating(rating);
        ratingRepository.save(existingRating);
    }
}
