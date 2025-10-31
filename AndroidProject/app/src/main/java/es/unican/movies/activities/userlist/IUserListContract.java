package es.unican.movies.activities.userlist;

import java.util.List;

import es.unican.movies.model.MovieInList;

public interface IUserListContract {

    interface View {
        void showMovies(List<MovieInList> movies);
        void showLoadError();
        void showEmptyList();
    }

    interface Presenter {
        void init(View view);
        void loadMovies();
        void onDeleteAllClicked();
    }

}
