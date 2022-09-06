package com.counsulteer.coolerimdb.dto.movie;

import com.counsulteer.coolerimdb.dto.actor.BasicActorDto;
import com.counsulteer.coolerimdb.entity.Genre;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UpdateMovieDto {
    private String title;
    private String image;
    private String description;
    private String yearOfRelease;
    private List<Genre> genres;
    private List<BasicActorDto> actors;

}
