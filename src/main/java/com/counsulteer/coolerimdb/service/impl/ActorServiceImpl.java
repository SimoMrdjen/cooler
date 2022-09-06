package com.counsulteer.coolerimdb.service.impl;

import com.counsulteer.coolerimdb.dto.actor.ActorDto;
import com.counsulteer.coolerimdb.dto.actor.BasicActorDto;
import com.counsulteer.coolerimdb.dto.actor.CreateActorDto;
import com.counsulteer.coolerimdb.dto.actor.UpdateActorDto;
import com.counsulteer.coolerimdb.entity.Actor;
import com.counsulteer.coolerimdb.entity.SortActorsBy;
import com.counsulteer.coolerimdb.exception.BadRequestException;
import com.counsulteer.coolerimdb.exception.NotFoundException;
import com.counsulteer.coolerimdb.mapper.ActorMapper;
import com.counsulteer.coolerimdb.mapper.MovieMapper;
import com.counsulteer.coolerimdb.repository.ActorRepository;
import com.counsulteer.coolerimdb.repository.MovieRepository;
import com.counsulteer.coolerimdb.service.ActorService;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class ActorServiceImpl implements ActorService {
    private final String ACTOR_NOT_FOUND = "Actor not found!";
    private final String MOVIE_NOT_FOUND = "Movie not found!";
    private static final String INVALID_PAGE_NUMBER = "Invalid page number!";
    private static final String INVALID_PAGE_SIZE = "Invalid page size!";
    private final ActorRepository actorRepository;
    private final MovieRepository movieRepository;
    private final ActorMapper actorMapper;
    private final MovieMapper movieMapper;

    @Override
    public ActorDto getActor(Long id) {
        Actor actor = actorRepository.findById(id).orElseThrow(() -> new NotFoundException(ACTOR_NOT_FOUND));
        ActorDto actorDto = actorMapper.mapEntityToDto(actor);
        actorDto.setMovies(actor.getMovies().stream().map(movieMapper::mapEntityToBasicDto).collect(Collectors.toList()));
        return actorDto;
    }

    @Override
    public List<ActorDto> getActors() {
        return actorRepository.findAll().stream().map(actor -> {
            ActorDto actorDto = actorMapper.mapEntityToDto(actor);
            actorDto.setMovies(actor.getMovies().stream().map(movieMapper::mapEntityToBasicDto).collect(Collectors.toList()));
            return actorDto;
        }).collect(Collectors.toList());
    }

    @Override
    public Page<BasicActorDto> getSortedActors(Integer page, Integer size, Optional<SortActorsBy> sortActorsBy, Optional<Sort.Direction> sortingOrder) {
        if (page < 0) throw new BadRequestException(INVALID_PAGE_NUMBER);
        if (size < 1) throw new BadRequestException(INVALID_PAGE_SIZE);
        AtomicReference<SortActorsBy> sortActorsByAtomicReference = new AtomicReference<>(SortActorsBy.NAME);
        AtomicReference<Sort.Direction> sortingOrderAtomicReference = new AtomicReference<>(Sort.Direction.DESC);
        sortActorsBy.ifPresent(sortActorsByAtomicReference::set);
        sortingOrder.ifPresent(sortingOrderAtomicReference::set);
        return actorRepository.findAll(PageRequest.of(page, size, sortingOrderAtomicReference.get(), sortActorsByAtomicReference.get().value())).map(actorMapper::mapEntityToBasicDto);
    }

    @Override
    @Transactional
    public ActorDto createActor(CreateActorDto createActorDto) {
        Actor actor = actorMapper.mapCreateDtoToEntity(createActorDto);
        Actor finalActor = actor;
        createActorDto.getMovies().forEach(movie -> finalActor.addMovie(movieRepository.findById(movie.getId()).orElseThrow(() -> new NotFoundException(MOVIE_NOT_FOUND))));
        actor = actorRepository.save(finalActor);
        ActorDto actorDto = actorMapper.mapEntityToDto(actor);
        actorDto.setMovies(actor.getMovies().stream().map(movieMapper::mapEntityToBasicDto).collect(Collectors.toList()));
        return actorDto;
    }

    @Override
    @Transactional
    public ActorDto updateActor(UpdateActorDto updateActorDto, Long id) {
        Actor actor = actorRepository.findById(id).orElseThrow(() -> new NotFoundException(ACTOR_NOT_FOUND));
        actorMapper.updateEntity(actor, updateActorDto);
        Actor finalActor = actor;
        if (Objects.nonNull(updateActorDto.getMovies())) {
            updateActorDto.getMovies().forEach(movie -> {
                if (finalActor.getMovies().stream().noneMatch(m -> movie.getId().equals(m.getId()))) {
                    finalActor.addMovie(movieRepository.findById(movie.getId()).orElseThrow(() -> new NotFoundException(MOVIE_NOT_FOUND)));
                }
            });

            int i;
            for (i = 0; i < finalActor.getMovies().size(); i++) {
                int x = i;
                if (updateActorDto.getMovies().stream().noneMatch(m -> (m.getId().equals(finalActor.getMovies().get(x).getId())))) {
                    finalActor.deleteMovie(finalActor.getMovies().get(x));
                }
            }
        }
        actor = actorRepository.save(finalActor);
        ActorDto actorDto = actorMapper.mapEntityToDto(actor);
        actorDto.setMovies(actor.getMovies().stream().map(movieMapper::mapEntityToBasicDto).collect(Collectors.toList()));
        return actorDto;
    }

    @Override
    public void deleteActor(Long id) {
        actorRepository.findById(id).ifPresentOrElse((actor) -> {
            while (actor.getMovies().size() != 0) actor.getMovies().get(0).deleteActor(actor);
            actorRepository.deleteById(id);
        }, () -> {
            throw new NotFoundException(ACTOR_NOT_FOUND);
        });
    }

}
