package com.counsulteer.coolerimdb.mapper;

import com.counsulteer.coolerimdb.dto.actor.ActorDto;
import com.counsulteer.coolerimdb.dto.actor.BasicActorDto;
import com.counsulteer.coolerimdb.dto.actor.CreateActorDto;
import com.counsulteer.coolerimdb.dto.actor.UpdateActorDto;
import com.counsulteer.coolerimdb.entity.Actor;

import java.util.ArrayList;

public class ActorMapper {

    public Actor mapCreateDtoToEntity(CreateActorDto createActorDto) {
        return new Actor(null, createActorDto.getFullName(), createActorDto.getBirthday(), createActorDto.getImage(), new ArrayList<>());
    }

    public ActorDto mapEntityToDto(Actor actor) {
        return new ActorDto(actor.getId(), actor.getFullName(), actor.getBirthday(), actor.getImage(), new ArrayList<>());
    }

    public void updateEntity(Actor actor, UpdateActorDto updateActorDto) {
        if (updateActorDto.getFullName() != null) {
            actor.setFullName(updateActorDto.getFullName());
        }
        if (updateActorDto.getBirthday() != null) {
            actor.setBirthday(updateActorDto.getBirthday());
        }
        if (updateActorDto.getImage() != null) {
            actor.setImage(updateActorDto.getImage());
        }
    }


    public BasicActorDto mapEntityToBasicDto(Actor actor) {
        return new BasicActorDto(actor.getId(), actor.getFullName(), actor.getBirthday(), actor.getImage());

    }

    public Actor mapBasicDtoToEntity(BasicActorDto basicActorDto) {
        return new Actor(basicActorDto.getId(), basicActorDto.getFullName(), basicActorDto.getBirthday(), basicActorDto.getImage(), new ArrayList<>());
    }


}
