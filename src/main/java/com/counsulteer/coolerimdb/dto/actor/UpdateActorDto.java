package com.counsulteer.coolerimdb.dto.actor;

import com.counsulteer.coolerimdb.dto.movie.BasicMovieDto;
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
public class UpdateActorDto {
    private String fullName;
    private LocalDate birthday;
    private String image;
    private List<BasicMovieDto> movies;
}
