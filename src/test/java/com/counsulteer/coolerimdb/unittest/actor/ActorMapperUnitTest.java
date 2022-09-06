package com.counsulteer.coolerimdb.unittest.actor;

import com.counsulteer.coolerimdb.dto.actor.ActorDto;
import com.counsulteer.coolerimdb.dto.actor.BasicActorDto;
import com.counsulteer.coolerimdb.dto.actor.CreateActorDto;
import com.counsulteer.coolerimdb.dto.actor.UpdateActorDto;
import com.counsulteer.coolerimdb.entity.Actor;
import com.counsulteer.coolerimdb.mapper.ActorMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class ActorMapperUnitTest {
    private final ActorMapper actorMapper = new ActorMapper();
    private CreateActorDto createActorDto;
    private UpdateActorDto updateActorDto;
    private Actor actor;
    private ActorDto actorDto;
    private BasicActorDto basicActorDto;

    @BeforeEach
    void beforeEach() {
        createActorDto = new CreateActorDto("Keanu Reeves", LocalDate.of(1964, 9, 2), "abc", new ArrayList<>());
        updateActorDto = new UpdateActorDto("Danzel Washington", LocalDate.of(1954, 12, 28), "cba", new ArrayList<>());
        actor = new Actor(1L, "Keanu Reeves", LocalDate.of(1964, 9, 2), "abc", new ArrayList<>());
        actorDto = new ActorDto(1L, "Keanu Reeves", LocalDate.of(1964, 9, 2), "abc", new ArrayList<>());
        basicActorDto = new BasicActorDto(1L, "Keanu Reeves", LocalDate.of(1964, 9, 2), "abc");
    }

    @Test
    public void shouldMapEntityToDtoWhenCalled() {
        assertThat(actorMapper.mapEntityToDto(actor)).isEqualTo(actorDto);
    }

    @Test
    public void shouldMapCreateDtoToEntityWhenCalled() {
        actor.setId(null);
        assertThat(actorMapper.mapCreateDtoToEntity(createActorDto)).isEqualTo(actor);
    }

    @Test
    public void shouldUpdateEntityWhenCalled() {
        actorMapper.updateEntity(actor, updateActorDto);
        assertThat(actor).isEqualTo(new Actor(1L, "Danzel Washington", LocalDate.of(1954, 12, 28), "cba", new ArrayList<>()));
    }

    @Test
    public void shouldMapEntityToBasicDtoCalled() {
        assertThat(actorMapper.mapEntityToBasicDto(actor)).isEqualTo(basicActorDto);
    }

    @Test
    public void shouldMapBasicDtoToEntityWhenCalled() {
        assertThat(actorMapper.mapBasicDtoToEntity(basicActorDto)).isEqualTo(actor);
    }
}
