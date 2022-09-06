package com.counsulteer.coolerimdb.dto.watchlist;

import com.counsulteer.coolerimdb.dto.movie.BasicMovieDto;
import lombok.Setter;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Setter
@Getter
@NotNull
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class WatchlistDto {
    private Long id;
    private String name;
    private Set<BasicMovieDto> movies;
}
