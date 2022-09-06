package com.counsulteer.coolerimdb.dto.watchlist;

import com.counsulteer.coolerimdb.dto.movie.BasicMovieDto;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class CreateWatchlistDto {
    @NotNull
    private String name;
    @NotNull
    private Set<BasicMovieDto> movies;
}
