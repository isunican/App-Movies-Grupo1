package es.unican.movies.activities.main.filters;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import es.unican.movies.model.Genres;
import es.unican.movies.model.Movie;

public class FiltersByGenres {
    private final List<Genres> genresToFilter;
    private final List<Movie> movies;

    public FiltersByGenres(List<Genres> genresToFilter, List<Movie> movies) {
        this.genresToFilter = genresToFilter;
        this.movies = movies;
    }

    public List<Movie> filter() {
        if (genresToFilter == null || genresToFilter.isEmpty() || movies == null) {
            return movies;
        }

        Set<String> selectedGenreNames = genresToFilter.stream()
                .map(Genres::getName)
                .collect(Collectors.toSet());

        return movies.stream()
                .filter(movie -> movie.getGenres() != null &&
                        movie.getGenres().stream()
                                .map(Genres::getName)
                                .anyMatch(selectedGenreNames::contains))
                .collect(Collectors.toList());
    }
}
