package es.unican.movies.activities.userlist;

import java.util.List;
import es.unican.movies.model.MovieInList;

/**
 * Contrato que define la comunicación entre la Vista y el Presentador
 * para la pantalla de la lista de películas de un usuario.
 */
public interface IUserListContract {

    interface Presenter {
        void init(View view);
        void onMenuInfoClicked();
        void onFilterStatusMenuClicked();
        void onStatusFiltered(List<String> selectedStatus);
    }

    interface View {
        void showMovies(List<MovieInList> movies);
        void showLoadCorrect(int movieCount);
        void showLoadError();
        void showInfoActivity();
        void showFilterByStatusDialog(List<String> statusesWithCount, List<String> selectedStatuses);
    }
}
