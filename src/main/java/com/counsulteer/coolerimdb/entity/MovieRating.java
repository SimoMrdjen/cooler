package com.counsulteer.coolerimdb.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;

@Entity
@Table(name = "\"movie_rating\"")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MovieRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "rating", nullable = false)
    private Rating rating;

    @Column(name = "movie_id", nullable = false)
    private Long movieId;

    public MovieRating(Long userId, Rating rating, Long movieId) {
        this.userId = userId;
        this.rating = rating;
        this.movieId = movieId;
    }
}
