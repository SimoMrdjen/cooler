package com.counsulteer.coolerimdb.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.List;

@Entity(name = "Actor")
@Table(name = "\"actor\"")
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Actor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "full_name", nullable = false)
    private String fullName;
    @Column(name = "birthday", nullable = false)
    private LocalDate birthday;
    @Column(name = "image")
    private String image;
    @ManyToMany(mappedBy = "actors")
    private List<Movie> movies;
    public Actor(String fullName, LocalDate birthday, String image) {
        this.fullName = fullName;
        this.birthday = birthday;
        this.image = image;
    }
    public void addMovie(Movie movie){
        movies.add(movie);
        movie.getActors().add(this);
    }
    public void deleteMovie(Movie movie){
        movies.remove(movie);
        movie.getActors().remove(this);
    }
}
