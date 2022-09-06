package com.counsulteer.coolerimdb.entity;

import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GenerationType;
import javax.persistence.GeneratedValue;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import java.util.Objects;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "\"watchlist\"")
@NoArgsConstructor
@AllArgsConstructor
public class Watchlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "watchlist_movies", nullable = false)
    private Set<Movie> movies;

    @Override
    public String toString() {
        return "Watchlist{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", movies=" + movies +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Watchlist watchlist = (Watchlist) o;
        return Objects.equals(id, watchlist.id) && name.equals(watchlist.name) && Objects.equals(movies, watchlist.movies);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, movies);
    }
}
