package es.unican.movies.activities.main;

import java.util.List;

import es.unican.movies.model.Movie;
import es.unican.movies.service.IMoviesRepository;

public interface IMainContract {

    interface Presenter {
        void init(View view);
        void onItemClicked(Movie movie);
        void onMenuInfoClicked();
        void onFilterGenreMenuClicked();
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

<<<<<<< HEAD
        void showFilterByGenreActivity(List<String> genresWithCount, List<String> selectedGenres);
=======
        void showFilterGenresDialog(List<String> genresWithCount, List<String> selectedGenres);
>>>>>>> e9761b7b873f5aea269a746f7126585e01cae0a8

        void showFilterDecadesDialog(List<String> decadesWithCount, List<String> selectedDecades);
    }
}
