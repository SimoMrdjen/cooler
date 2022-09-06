package com.counsulteer.coolerimdb.dto.movie;

import com.counsulteer.coolerimdb.dto.actor.BasicActorDto;
import com.counsulteer.coolerimdb.entity.Genre;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CreateMovieDto {
    @NotNull
    private String title;
    @NotNull
    private String image;
    @NotNull
    private String description;
    @NotNull
    private String yearOfRelease;
    @NotNull
    private List<Genre> genres;
    @NotNull
    private List<BasicActorDto> actors;
}