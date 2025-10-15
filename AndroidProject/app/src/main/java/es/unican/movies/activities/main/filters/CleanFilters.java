package es.unican.movies.activities.main.filters;

import java.util.List;

import es.unican.movies.model.Genres;
import es.unican.movies.model.Movie;

import java.util.List;

public class CleanFilters {

    private final List<Genres> genresToFilter; // Lista de filtros seleccionados
    private final List<Movie> allMovies;       // Lista completa de películas

    public CleanFilters(List<Genres> genresToFilter, List<Movie> allMovies) {
        this.genresToFilter = genresToFilter;
        this.allMovies = allMovies;
    }

    // Método para limpiar los filtros
    public List<Movie> clear() {
        if (genresToFilter != null) {
            genresToFilter.clear(); // Borra todos los filtros
        }

        // Devuelve la lista completa de películas sin filtrar
        return allMovies;
    }
}

