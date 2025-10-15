package es.unican.movies.activities.details;

import es.unican.movies.model.Movie;

public interface IDetailsContract {

    interface View {
        Movie getMovie();

        void showMovieInfo(String title, String releaseDate, String duration, String genres, String voteAverage, String summaryScore, String posterPath);
    }

    interface Presenter {
        void init();
    }
}
