package com.counsulteer.coolerimdb.dto.watchlist;

import com.counsulteer.coolerimdb.dto.movie.BasicMovieDto;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.Getter;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@EqualsAndHashCode
public class UpdateWatchlistDto {
    @NotNull
    private String name;
    @NotNull
    private Set<BasicMovieDto> movies;
}
