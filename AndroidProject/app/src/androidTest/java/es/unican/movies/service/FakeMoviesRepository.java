
package es.unican.movies.service;

import java.util.ArrayList;
import java.util.List;

import es.unican.movies.model.Movie;
import es.unican.movies.model.Series;

public class FakeMoviesRepository implements IMoviesRepository {

    private final List<Movie> movies;
    private final List<Series> series;

    public FakeMoviesRepository() {
        // Initialize with some test data
        movies = new ArrayList<>();
        // Add some fake movies
        // movies.add(new Movie(1, "Fake Movie 1", "Overview 1"));

        series = new ArrayList<>();
        // Add some fake series
        // series.add(new Series(1, "Fake Series 1", "Overview 1"));
    }

    @Override
    public void requestAggregateMovies(ICallback<List<Movie>> cb) {
        cb.onSuccess(movies);
    }

    @Override
    public void requestAggregateSeries(ICallback<List<Series>> cb) {
        cb.onSuccess(series);
    }

    @Override
    public void requestMovieDetails(int id, ICallback<Movie> cb) {
        // Find movie by id and return it
        for (Movie movie : movies) {
            if (movie.getId() == id) {
                cb.onSuccess(movie);
                return;
            }
        }
        cb.onFailure(new Throwable("Movie not found"));
    }

    @Override
    public void requestSeriesDetails(int id, ICallback<Series> cb) {
        // Find series by id and return it
        for (Series s : series) {
            if (s.getId() == id) {
                cb.onSuccess(s);
                return;
            }
        }
        cb.onFailure(new Throwable("Series not found"));
    }
}
