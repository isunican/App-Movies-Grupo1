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
        void onFilterDecadeMenuClicked(); // New method for decade filter
        void onDecadesFiltered(List<String> selectedDecades);
        void onLimpiarFiltroMenuClicked();// New method for decade filter
    }

    interface View {
        void init();
        IMoviesRepository getMoviesRepository();
        void showMovies(List<Movie> movies);
        void showLoadCorrect(int movies);
        void showLoadError();
        void showMovieDetails(Movie movie);
        void showInfoActivity();

        void showFilterByGenreActivity(List<String> genresWithCount, List<String> selectedGenres);
        void showFilterByDecadeActivity(List<String> decadesWithCount, List<String> selectedDecades); // New method for decade filter

        //void showGenreFilterDialog(List<String> allGenres, List<String> selectedGenres);
    }
}
