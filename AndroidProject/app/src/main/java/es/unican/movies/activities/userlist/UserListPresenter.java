package es.unican.movies.activities.userlist;

import java.util.List;
import javax.inject.Inject;
import dagger.hilt.android.scopes.ActivityScoped;
import es.unican.movies.model.MovieInList;
import es.unican.movies.model.MovieInListDao;

@ActivityScoped
public class UserListPresenter implements IUserListContract.Presenter {

    private IUserListContract.View view;
    private final MovieInListDao movieDao;

    @Inject
    public UserListPresenter(MovieInListDao movieDao) {
        this.movieDao = movieDao;
    }

    @Override
    public void init(IUserListContract.View view) {
        this.view = view;
        loadMovies();
    }

    @Override
    public void loadMovies() {
        // Room queries must be run on a background thread.
        new Thread(() -> {
            try {
                List<MovieInList> movies = movieDao.getAll();
                if (movies.isEmpty()) {
                    view.showEmptyList();
                } else {
                    view.showMovies(movies);
                }
            } catch (Exception e) {
                view.showLoadError();
            }
        }).start();
    }
}
