package com.counsulteer.coolerimdb.dto.actor;

import com.counsulteer.coolerimdb.dto.movie.BasicMovieDto;
import com.counsulteer.coolerimdb.dto.movie.MovieDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CreateActorDto {
    @NotNull
    private String fullName;
    @NotNull
    private LocalDate birthday;
    @NotNull
    private String image;
    @NotNull
    private List<BasicMovieDto> movies;
}
