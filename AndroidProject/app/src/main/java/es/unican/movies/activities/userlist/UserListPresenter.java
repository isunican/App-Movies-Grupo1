package es.unican.movies.activities.userlist;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import es.unican.movies.model.MovieInList;

/**
 * Implementación del presentador para la lista de películas de un usuario.
 * Se encarga de la lógica de la vista y de la gestión de los datos.
 */
public class UserListPresenter implements IUserListContract.Presenter {

    private IUserListContract.View view;
    private List<MovieInList> allMovies;
    private List<MovieInList> displayedMovies;
    private List<String> selectedStatusForFilter = new ArrayList<>();

    /**
     * Constructor del presentador.
     */
    public UserListPresenter() {
        // Constructor vacío
    }

    /**
     * Inicializa el presentador. Carga los datos de prueba y los muestra en la vista.
     * @param view La vista con la que el presentador se comunica.
     */
    @Override
    public void init(IUserListContract.View view) {
        this.view = view;

        // Datos de ejemplo para probar:
        allMovies = new ArrayList<>();

        MovieInList movie1 = new MovieInList();
        movie1.setTitle("Movie 1 - Terminada");
        movie1.setStatus("Terminado");
        allMovies.add(movie1);

        MovieInList movie2 = new MovieInList();
        movie2.setTitle("Movie 2 - En proceso");
        movie2.setStatus("En proceso");
        allMovies.add(movie2);

        MovieInList movie3 = new MovieInList();
        movie3.setTitle("Movie 3 - Sin empezar");
        movie3.setStatus("Sin empezar");
        allMovies.add(movie3);

        MovieInList movie4 = new MovieInList();
        movie4.setTitle("Movie 4 - Terminada");
        movie4.setStatus("Terminado");
        allMovies.add(movie4);

        MovieInList movie5 = new MovieInList();
        movie5.setTitle("Movie 5 - Sin estado");
        movie5.setStatus(null); // Se tratará como "Sin empezar"
        allMovies.add(movie5);

        applyFilters();
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
