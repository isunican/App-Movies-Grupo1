package es.unican.movies.activities.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import es.unican.movies.activities.main.filters.FiltersByGenres;
import es.unican.movies.model.Genres;
import es.unican.movies.model.Movie;
import es.unican.movies.service.ICallback;
import es.unican.movies.service.IMoviesRepository;

public class MainPresenter implements IMainContract.Presenter {

    private IMainContract.View view;
    private List<Movie> allMovies;
    private List<Movie> displayedMovies;
    private List<String> selectedGenresForFilter = new ArrayList<>();
    private final Map<String, Integer> genreCounts = new HashMap<>();

    /**
     * Inicializa el presentador, estableciendo la conexión con la vista.
     * Este método debe ser llamado por la vista (Activity) en su fase de creación.
     * Guarda una referencia a la vista, la inicializa y comienza la carga
     * inicial de películas.
     *
     * @param view La instancia de la vista que este presentador controlará.
     */
    @Override
    public void init(IMainContract.View view) {
        this.view = view;
        this.view.init();
        load();
    }

    /**
     * Solicita el listado completo de películas al repositorio.
     * En caso de éxito, actualiza las listas de películas (tanto la completa como
     * la que se muestra) y le ordena a la vista que las muestre.
     * En caso de fallo, le ordena a la vista que muestre un mensaje de error.
     */
    private void load() {
        IMoviesRepository repository = view.getMoviesRepository();
        repository.requestAggregateMovies(new ICallback<List<Movie>>() {
            @Override
            public void onSuccess(List<Movie> elements) {
                allMovies = elements;
                displayedMovies = new ArrayList<>(elements);
                view.showMovies(displayedMovies);
                view.showLoadCorrect(displayedMovies.size());
            }

            @Override
            public void onFailure(Throwable e) {
                view.showLoadError();
            }
        });
    }

    /**
     * Se invoca cuando el usuario solicita abrir el filtro por géneros.
     * Calcula la cantidad de películas por cada género (incluyendo la categoría "NA"
     * para películas sin género), ordena los géneros de más a menos en cantidad y
     * finalmente le pide a la vista que muestre el diálogo de filtrado con los datos preparados.
     */
    @Override
    public void onFilterGenreMenuClicked() {
        if (allMovies == null || allMovies.isEmpty()) {
            return;
        }

        genreCounts.clear();
        for (Movie movie : allMovies) {
            if (movie.getGenres() == null || movie.getGenres().isEmpty()) {
                genreCounts.put("NA", genreCounts.getOrDefault("NA", 0) + 1);
            } else {
                for (Genres genre : movie.getGenres()) {
                    genreCounts.put(genre.getName(), genreCounts.getOrDefault(genre.getName(), 0) + 1);
                }
            }
        }

        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(genreCounts.entrySet());
        sortedEntries.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        List<String> formattedGenres = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : sortedEntries) {
            if (entry.getValue() > 0) {
                formattedGenres.add(String.format("%s (%d)", entry.getKey(), entry.getValue()));
            }
        }

        // 🔹 Pasamos la lista de géneros a la vista
<<<<<<< HEAD
        view.showFilterByGenreActivity(formattedGenres, selectedGenresForFilter);
=======
        view.showFilterGenresDialog(formattedGenres, selectedGenresForFilter);
>>>>>>> e9761b7b873f5aea269a746f7126585e01cae0a8
    }

    /**
     * Aplica el filtro por géneros seleccionado por el usuario.
     * Este método recibe la lista de géneros seleccionados desde la vista,
     * procesa la lista para filtrar las películas y luego las ordena según
     * la popularidad del género. Finalmente, actualiza la vista para que
     * muestre solo las películas filtradas y ordenadas.
     *
     * @param selectedGenresWithCount La lista de géneros seleccionados por el usuario,
     *                                incluyendo el conteo (ej. "Action (15)").
     */
    @Override
    public void onGenresFiltered(List<String> selectedGenresWithCount) {
        selectedGenresForFilter = selectedGenresWithCount;
        if (selectedGenresWithCount == null || selectedGenresWithCount.isEmpty()) {
            displayedMovies = new ArrayList<>(allMovies);
        } else {
            List<String> cleanSelectedGenres = selectedGenresWithCount.stream()
                    .map(nameWithCount -> nameWithCount.replaceAll("\\s*\\(\\d+\\)$", "").trim())
                    .collect(Collectors.toList());

            boolean isNaSelected = cleanSelectedGenres.contains("NA");
            List<String> realGenreNames = cleanSelectedGenres.stream().filter(g -> !g.equals("NA")).collect(Collectors.toList());

            List<Movie> finalFilteredList = new ArrayList<>();

            if (!realGenreNames.isEmpty()) {
                List<Genres> genresToFilter = realGenreNames.stream().map(name -> {
                    Genres g = new Genres();
                    g.setName(name);
                    return g;
                }).collect(Collectors.toList());
                FiltersByGenres filter = new FiltersByGenres(genresToFilter, allMovies);
                finalFilteredList.addAll(filter.filter());
            }

            if (isNaSelected) {
                allMovies.stream()
                        .filter(m -> m.getGenres() == null || m.getGenres().isEmpty())
                        .forEach(finalFilteredList::add);
            }

            displayedMovies = new ArrayList<>(new HashSet<>(finalFilteredList)); // Eliminar duplicados si los hubiera

            Set<String> cleanSelectedGenresSet = new HashSet<>(cleanSelectedGenres);
            displayedMovies.sort((movie1, movie2) -> {
                int rank1 = getMovieGenreRank(movie1, cleanSelectedGenresSet);
                int rank2 = getMovieGenreRank(movie2, cleanSelectedGenresSet);
                return Integer.compare(rank2, rank1);
            });
        }

        view.showMovies(displayedMovies);
        view.showLoadCorrect(displayedMovies.size());
    }

    /**
     * Calcula un "rango" o "puntuación de popularidad" para una película, basado en los
     * géneros seleccionados para el filtro. El rango es igual al número de películas
     * que tiene el género más popular de la película (dentro de los seleccionados).
     * Este método es utilizado para ordenar la lista de películas filtradas.
     *
     * @param movie La película a la que se le calculará el rango.
     * @param selectedGenres El conjunto de géneros (sin conteo) que el usuario ha seleccionado.
     * @return Un entero que representa la popularidad de la película para la ordenación.
     */

    private int getMovieGenreRank(Movie movie, Set<String> selectedGenres) {
        if (movie.getGenres() == null || movie.getGenres().isEmpty()) {
            return selectedGenres.contains("NA") ? genreCounts.getOrDefault("NA", 0) : 0;
        }
        int maxRank = 0;
        for (Genres genre : movie.getGenres()) {
            if (selectedGenres.contains(genre.getName())) {

                int rank = genreCounts.getOrDefault(genre.getName(), 0);
                if (rank > maxRank) {
                    maxRank = rank;
                }
            }
        }
        return maxRank;
    }

    /**
     * Gestiona la acción de clic sobre una película en la lista principal.
     * Le ordena a la vista que navegue a la pantalla de detalles de la
     * película seleccionada.
     *
     * @param movie La película sobre la que el usuario ha hecho clic.
     */
    @Override
    public void onItemClicked(Movie movie) {
        if (movie == null) {
            return;
        }
        view.showMovieDetails(movie);
    }

    /**
     * Gestiona la acción de clic sobre el ícono de información en la barra de
     * acciones. Le ordena a la vista que navegue a la pantalla de información.
     *
     */
    @Override
    public void onMenuInfoClicked() {
        view.showInfoActivity();
    }
}
