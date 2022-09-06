package com.counsulteer.coolerimdb.dto.movie;

import com.counsulteer.coolerimdb.entity.Genre;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BasicMovieDto {
    private Long id;
    private String title;
    private String image;
    private String description;
    private Integer likes;
    private Integer dislikes;
    private Integer rating;
    private String yearOfRelease;
    private LocalDate dateOfCreation;
    private List<Genre> genres;
}
