package com.counsulteer.coolerimdb.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "\"movie\"")
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "image", nullable = false)
    private String image;
    @Column(name = "description", nullable = false, length = 500)
    private String description;
    @Column(name = "likes")
    private Integer likes;
    @Column(name = "dislikes")
    private Integer dislikes;
    @Column(name = "rating")
    private Integer rating;
    @Column(name = "year_of_release", nullable = false)
    private String yearOfRelease;
    @Column(name = "date_of_creation", nullable = false)
    private LocalDate dateOfCreation;
    @ElementCollection
    @CollectionTable(name = "movie_genre", joinColumns = @JoinColumn(name = "movie_id"))
    @Enumerated(EnumType.STRING)
    private List<Genre> genres;
    @ManyToMany
    @JoinTable(name = "movie_roles",
            joinColumns = {@JoinColumn(name = "movie_id")},
            inverseJoinColumns = {@JoinColumn(name = "actor_id")})
    private List<Actor> actors;

    public Movie(Long id, String title, String image, String description, String yearOfRelease, List<Genre> genres, List<Actor> actors) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.description = description;
        this.likes = 0;
        this.dislikes = 0;
        this.rating = 0;
        this.yearOfRelease = yearOfRelease;
        this.dateOfCreation = LocalDate.now();
        this.genres = genres;
        this.actors = actors;
    }

    public void incrementLikes() {
        this.likes++;
    }

    public void decrementLikes() {
        this.likes--;
    }

    public void incrementDislikes() {
        this.dislikes++;
    }

    public void decrementDislikes() {
        this.dislikes--;
    }

    public void updateRating() {
        this.rating = this.likes - this.dislikes;
    }

    public void addActor(Actor actor) {
        actors.add(actor);
        actor.getMovies().add(this);
    }

    public void deleteActor(Actor actor) {
        actors.remove(actor);
        actor.getMovies().remove(this);
    }

}
