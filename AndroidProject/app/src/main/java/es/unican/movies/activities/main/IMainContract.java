package es.unican.movies.activities.main;

import java.util.List;

import es.unican.movies.model.Movie;
import es.unican.movies.service.IMoviesRepository;

public interface IMainContract {

    interface Presenter {
        void init(View view);
        void onItemClicked(Movie movie);
        void onMenuInfoClicked();
        void onFilterMenuClicked();
        void onGenresFiltered(List<String> selectedGenres);
    }

    interface View {
        void init();
        IMoviesRepository getMoviesRepository();
        void showMovies(List<Movie> movies);
        void showLoadCorrect(int movies);
        void showLoadError();
        void showMovieDetails(Movie movie);
        void showInfoActivity();

        void showFilterActivity(List<String> genresWithCount, List<String> selectedGenres);

        //void showGenreFilterDialog(List<String> allGenres, List<String> selectedGenres);
    }
}
