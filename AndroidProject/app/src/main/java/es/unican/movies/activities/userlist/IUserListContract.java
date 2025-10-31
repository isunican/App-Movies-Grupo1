package es.unican.movies.activities.userlist;

import java.util.List;

import es.unican.movies.model.MovieInList;

/**
 * El contrato IUserListContract define la comunicación entre la Vista (View)
 * y el Presentador (Presenter) para la pantalla de la lista de películas del usuario,
 * siguiendo el patrón de arquitectura Modelo-Vista-Presentador (MVP).
 */
public interface IUserListContract {

    /**
     * La interfaz View es implementada por la actividad (UserListView) y define los métodos
     * que el Presentador puede invocar para actualizar la interfaz de usuario.
     */
    interface View {
        /**
         * Muestra la lista de películas en la interfaz de usuario.
         *
         * @param movies La lista de películas para mostrar.
         */
        void showMovies(List<MovieInList> movies);

        /**
         * Muestra un mensaje de error si falla la carga de las películas.
         */
        void showLoadError();

        /**
         * Muestra un mensaje o un estado visual que indica que la lista de películas está vacía.
         */
        void showEmptyList();
    }

    /**
     * La interfaz Presenter es implementada por el presentador (UserListPresenter) y define
     * los métodos que la Vista puede invocar para notificar eventos del usuario.
     */
    interface Presenter {
        /**
         * Inicializa el presentador, estableciendo la conexión con la vista.
         *
         * @param view La instancia de la vista a la que este presentador está asociado.
         */
        void init(View view);

        /**
         * Carga las películas de la lista del usuario desde el repositorio de datos.
         */
        void loadMovies();

        void onDeleteAllClicked();
    }

}
