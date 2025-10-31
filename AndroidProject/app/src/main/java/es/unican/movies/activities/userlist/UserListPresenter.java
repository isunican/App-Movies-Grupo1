package es.unican.movies.activities.userlist;

import java.util.List;
import javax.inject.Inject;
import dagger.hilt.android.scopes.ActivityScoped;
import es.unican.movies.model.MovieInList;
import es.unican.movies.model.MovieInListDao;

/**
 * El UserListPresenter es el presentador para la pantalla de la lista de películas del usuario.
 * Se encarga de la lógica de negocio, como cargar las películas de la base de datos
 * y comunicarle a la vista (UserListView) cómo debe mostrar los datos.
 * Sigue el patrón de arquitectura Modelo-Vista-Presentador (MVP).
 */
@ActivityScoped
public class UserListPresenter implements IUserListContract.Presenter {

    private IUserListContract.View view;
    private final MovieInListDao movieDao;

    /**
     * Constructor para el UserListPresenter.
     *
     * @param movieDao El DAO para acceder a las películas en la lista del usuario.
     */
    @Inject
    public UserListPresenter(MovieInListDao movieDao) {
        this.movieDao = movieDao;
    }

    /**
     * Inicializa el presentador, estableciendo la conexión con la vista y cargando las películas.
     *
     * @param view La instancia de la vista a la que este presentador está asociado.
     */
    @Override
    public void init(IUserListContract.View view) {
        this.view = view;
        loadMovies();
    }

    /**
     * Carga las películas de la lista del usuario desde la base de datos de Room.
     * La consulta se ejecuta en un hilo secundario para evitar bloquear la interfaz de usuario.
     * Una vez cargadas, actualiza la vista con las películas o muestra un estado de lista vacía.
     */
    @Override
    public void loadMovies() {
        // Las consultas de Room deben ejecutarse en un hilo secundario.
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

    /**
     * Gestiona el evento de clic en el botón para eliminar todas las películas.
     * Ejecuta la operación de borrado en un hilo secundario y luego recarga la lista.
     */
    @Override
    public void onDeleteAllClicked() {
        new Thread(() -> {
            movieDao.deleteAll();
            loadMovies();
        }).start();
    }
}
