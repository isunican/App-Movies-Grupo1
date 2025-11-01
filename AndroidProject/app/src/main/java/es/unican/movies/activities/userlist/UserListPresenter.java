package es.unican.movies.activities.userlist;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import javax.inject.Inject;
import dagger.hilt.android.scopes.ActivityScoped;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
    private List<MovieInList> allMovies;
    private List<MovieInList> displayedMovies;
    private List<String> selectedStatusForFilter = new ArrayList<>();

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
        applyFilters();
    }

    /**
     * Carga las películas de la lista del usuario desde la base de datos de Room.
     * La consulta se ejecuta en un hilo secundario para evitar bloquear la interfaz de usuario.
     * Una vez cargadas, actualiza la vista con las películas o muestra un estado de lista vacía.
     */
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

    /**
     * Gestiona el clic en el botón de información del menú.
     */
    @Override
    public void onMenuInfoClicked() {
        view.showInfoActivity();
    }

    /**
     * Gestiona el clic en el botón de filtrar por estado. Calcula el conteo de películas
     * por cada estado y solicita a la vista que muestre el diálogo de filtro.
     */
    @Override
    public void onFilterStatusMenuClicked() {
        if (allMovies == null) {
            view.showLoadError();
            return;
        }

        Map<String, Integer> statusCounts = new LinkedHashMap<>();
        statusCounts.put("Terminado", 0);
        statusCounts.put("En proceso", 0);
        statusCounts.put("Sin empezar", 0);

        for (MovieInList movie : allMovies) {
            String status = movie.getStatus();
            if ("Terminado".equals(status)) {
                statusCounts.computeIfPresent("Terminado", (k, v) -> v + 1);
            } else if ("En proceso".equals(status)) {
                statusCounts.computeIfPresent("En proceso", (k, v) -> v + 1);
            } else {
                statusCounts.computeIfPresent("Sin empezar", (k, v) -> v + 1);
            }
        }

        List<String> formattedStatusList = statusCounts.entrySet().stream()
                .map(entry -> String.format("%s (%d)", entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        view.showFilterByStatusDialog(formattedStatusList, selectedStatusForFilter);
    }

    /**
     * Se llama cuando el usuario aplica un filtro de estado desde el diálogo.
     * @param selectedStatus La lista de estados que el usuario ha seleccionado.
     */
    @Override
    public void onStatusFiltered(List<String> selectedStatus) {
        this.selectedStatusForFilter = selectedStatus;
        applyFilters();
    }

    /**
     * Aplica los filtros seleccionados a la lista de todas las películas y actualiza la vista.
     */
    private void applyFilters() {
        if (allMovies == null) return;

        List<MovieInList> filteredMovies = new ArrayList<>(allMovies);
        filteredMovies = applyStatusFilter(filteredMovies);

        displayedMovies = filteredMovies;
        view.showMovies(displayedMovies);
        view.showLoadCorrect(displayedMovies.size());
    }

    /**
     * Filtra una lista de películas según los estados seleccionados.
     * @param movies La lista de películas a filtrar.
     * @return Una nueva lista con solo las películas que coinciden con el filtro.
     */
    private List<MovieInList> applyStatusFilter(List<MovieInList> movies) {
        if (selectedStatusForFilter == null || selectedStatusForFilter.isEmpty()) {
            return movies;
        }

        Set<String> cleanSelectedStatus = selectedStatusForFilter.stream()
                .map(status -> status.replaceAll("\\s*\\(\\d+\\)$", "").trim())
                .collect(Collectors.toSet());

        return movies.stream().filter(movie -> {
            String status = movie.getStatus();
            if (status != null && !status.isEmpty()) {
                return cleanSelectedStatus.contains(status);
            } else {
                return cleanSelectedStatus.contains("Sin empezar");
            }
        }).collect(Collectors.toList());
    }
}

}
