package com.counsulteer.coolerimdb.service;

import com.counsulteer.coolerimdb.dto.actor.ActorDto;
import com.counsulteer.coolerimdb.dto.actor.BasicActorDto;
import com.counsulteer.coolerimdb.dto.actor.CreateActorDto;
import com.counsulteer.coolerimdb.dto.actor.UpdateActorDto;
import com.counsulteer.coolerimdb.entity.SortActorsBy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface ActorService {
    ActorDto getActor(Long id);

    List<ActorDto> getActors();

    Page<BasicActorDto> getSortedActors(Integer page, Integer size, Optional<SortActorsBy> sortActorsBy, Optional<Sort.Direction> sortingOrder);

    ActorDto createActor(CreateActorDto createActorDto);

    ActorDto updateActor(UpdateActorDto updateActorDto, Long id);

    void deleteActor(Long id);

}
