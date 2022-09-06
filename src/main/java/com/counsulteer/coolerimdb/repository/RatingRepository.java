package com.counsulteer.coolerimdb.repository;

import com.counsulteer.coolerimdb.entity.MovieRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<MovieRating, Long> {
    Optional<MovieRating> findByUserIdAndMovieId(Long userId, Long movieId);
}
