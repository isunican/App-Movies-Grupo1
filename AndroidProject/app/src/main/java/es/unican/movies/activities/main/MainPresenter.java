package es.unican.movies.activities.main;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import es.unican.movies.model.Genres;
import es.unican.movies.model.Movie;
import es.unican.movies.service.ICallback;
import es.unican.movies.service.IMoviesRepository;

/**
 * Implementación del presentador (Presenter) para la vista principal.
 * Se encarga de manejar la lógica de negocio y actuar como intermediario
 * entre la vista (MainView) y el modelo (repositorio de películas).
 */
public class MainPresenter implements IMainContract.Presenter {

    // La vista (View) con la que este presentador se comunica
    private IMainContract.View view;

    // Lista completa de todas las películas cargadas desde el repositorio
    private List<Movie> allMovies;

    // Lista de películas que se muestran actualmente en la UI, después de aplicar filtros
    private List<Movie> displayedMovies;

    // Listas que almacenan las selecciones de los filtros de género y década
    private List<String> selectedGenresForFilter = new ArrayList<>();
    private List<String> selectedDecadesForFilter = new ArrayList<>();

    /**
     * Inicializa el presentador. Establece la vista y comienza la carga de datos.
     * @param view La vista (MainView) que este presentador controlará.
     */
    @Override
    public void init(IMainContract.View view) {
        this.view = view;
        this.view.init();
        load();
    }

    /**
     * Realiza la petición al repositorio para obtener la lista de películas.
     * En caso de éxito, actualiza la lista de películas y notifica a la vista.
     * En caso de fallo, notifica a la vista para que muestre un error.
     */
    private void load() {
        IMoviesRepository repository = view.getMoviesRepository();
        repository.requestAggregateMovies(new ICallback<List<Movie>>() {
            @Override
            public void onSuccess(List<Movie> elements) {
                allMovies = elements;
                applyFilters(); // Muestra las películas sin filtros la primera vez
            }

            @Override
            public void onFailure(Throwable e) {
                view.showLoadError();
            }
        });
    }

    /**
     * Se invoca cuando el usuario pulsa la opción de filtrar por género.
     * Calcula cuántas películas de la lista (ya filtrada por década) pertenecen a cada género
     * y solicita a la vista que muestre el diálogo de selección de géneros.
     */
    @Override
    public void onFilterGenreMenuClicked() {
        if (allMovies == null) return;

        List<Movie> moviesToConsider = applyDecadeFilter(new ArrayList<>(allMovies));

        Set<String> allPossibleGenres = new HashSet<>();
        allPossibleGenres.add("NA"); // Género para películas sin género asignado
        allMovies.forEach(movie -> {
            if (movie.getGenres() != null && !movie.getGenres().isEmpty()) {
                movie.getGenres().forEach(genre -> allPossibleGenres.add(genre.getName()));
            }
        });

        Map<String, Integer> genreCounts = new HashMap<>();
        allPossibleGenres.forEach(genreName -> genreCounts.put(genreName, 0));

        for (Movie movie : moviesToConsider) {
            if (movie.getGenres() == null || movie.getGenres().isEmpty()) {
                genreCounts.computeIfPresent("NA", (k, v) -> v + 1);
            } else {
                for (Genres genre : movie.getGenres()) {
                    genreCounts.computeIfPresent(genre.getName(), (k, v) -> v + 1);
                }
            }
        }

        // Actualiza los contadores en la lista de géneros ya seleccionados
        Set<String> cleanSelectedGenres = selectedGenresForFilter.stream()
                .map(g -> g.replaceAll("\\s*\\(\\d+\\)$", "").trim())
                .collect(Collectors.toSet());

        List<String> updatedSelectedGenres = new ArrayList<>();
        for (String genreName : cleanSelectedGenres) {
            Integer newCount = genreCounts.get(genreName);
            if (newCount != null) {
                updatedSelectedGenres.add(String.format("%s (%d)", genreName, newCount));
            }
        }
        this.selectedGenresForFilter = updatedSelectedGenres;

        List<Map.Entry<String, Integer>> sortedGenres = new ArrayList<>(genreCounts.entrySet());
        sortedGenres.sort((e1, e2) -> {
            int countCompare = e2.getValue().compareTo(e1.getValue());
            return (countCompare != 0) ? countCompare : e1.getKey().compareTo(e2.getKey());
        });

        List<String> formattedGenres = sortedGenres.stream()
                .map(entry -> String.format("%s (%d)", entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        view.showFilterByGenreActivity(formattedGenres, selectedGenresForFilter);
    }

    /**
     * Se invoca cuando el usuario pulsa la opción de filtrar por década.
     * Calcula cuántas películas de la lista (ya filtrada por género) pertenecen a cada década
     * y solicita a la vista que muestre el diálogo de selección de décadas.
     */
    @Override
    public void onFilterDecadeMenuClicked() {
        if (allMovies == null) return;

        List<Movie> moviesToConsider = applyGenreFilter(new ArrayList<>(allMovies));

        Map<Integer, Integer> decadeCounts = new TreeMap<>();
        int lastDecade = (Calendar.getInstance().get(Calendar.YEAR) / 10) * 10;
        for (int decade = 1900; decade <= lastDecade; decade += 10) {
            decadeCounts.put(decade, 0);
        }

        for (Movie movie : moviesToConsider) {
            String yearStr = movie.getYear();
            if (yearStr != null && !yearStr.trim().isEmpty()) {
                try {
                    int year = Integer.parseInt(yearStr.trim());
                    int decade = (year / 10) * 10;
                    decadeCounts.computeIfPresent(decade, (k, v) -> v + 1);
                } catch (NumberFormatException e) {
                    // Ignora películas con formato de año inválido
                }
            }
        }

        // Actualiza los contadores en la lista de décadas ya seleccionadas
        Set<String> cleanSelectedDecades = selectedDecadesForFilter.stream()
                .map(d -> d.replaceAll("\\s*\\(\\d+\\)$", "").trim())
                .collect(Collectors.toSet());

        List<String> updatedSelectedDecades = new ArrayList<>();
        for (String decadeName : cleanSelectedDecades) {
            try {
                int decadeInt = Integer.parseInt(decadeName.replace("'s", ""));
                Integer newCount = decadeCounts.get(decadeInt);
                if (newCount != null) {
                    updatedSelectedDecades.add(String.format("%d's (%d)", decadeInt, newCount));
                }
            } catch (NumberFormatException e) {
                // Ignora si el formato no es el esperado
            }
        }
        this.selectedDecadesForFilter = updatedSelectedDecades;

        List<String> formattedDecades = decadeCounts.entrySet().stream()
                .map(entry -> String.format("%d's (%d)", entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        view.showFilterByDecadeActivity(formattedDecades, selectedDecadesForFilter);
    }

    /**
     * Callback que se ejecuta cuando el usuario aplica un filtro de géneros.
     * @param selectedGenresWithCount La lista de géneros seleccionados.
     */
    @Override
    public void onGenresFiltered(List<String> selectedGenresWithCount) {
        this.selectedGenresForFilter = selectedGenresWithCount;
        applyFilters();
    }

    /**
     * Callback que se ejecuta cuando el usuario aplica un filtro de décadas.
     * @param selectedDecadesWithCount La lista de décadas seleccionadas.
     */
    @Override
    public void onDecadesFiltered(List<String> selectedDecadesWithCount) {
        this.selectedDecadesForFilter = selectedDecadesWithCount;
        applyFilters();
    }

    /**
     * Aplica los filtros de género y década a la lista completa de películas
     * y actualiza la vista con el resultado.
     */
    private void applyFilters() {
        if (allMovies == null) return;

        List<Movie> filteredMovies = new ArrayList<>(allMovies);
        filteredMovies = applyGenreFilter(filteredMovies);
        filteredMovies = applyDecadeFilter(filteredMovies);

        displayedMovies = filteredMovies;

        // Si hay un filtro de género activo, ordena las películas según el "rango" del género
        if (selectedGenresForFilter != null && !selectedGenresForFilter.isEmpty()) {
            Map<String, Integer> currentCountsForSorting = new HashMap<>();
            for (Movie movie : displayedMovies) {
                if (movie.getGenres() == null || movie.getGenres().isEmpty()) {
                    currentCountsForSorting.put("NA", currentCountsForSorting.getOrDefault("NA", 0) + 1);
                } else {
                    for (Genres genre : movie.getGenres()) {
                        currentCountsForSorting.put(genre.getName(), currentCountsForSorting.getOrDefault(genre.getName(), 0) + 1);
                    }
                }
            }

            Set<String> cleanSelectedGenresSet = selectedGenresForFilter.stream()
                    .map(name -> name.replaceAll("\\s*\\(\\d+\\)$", "").trim())
                    .collect(Collectors.toSet());

            displayedMovies.sort((m1, m2) -> {
                int rank1 = getMovieGenreRank(m1, cleanSelectedGenresSet, currentCountsForSorting);
                int rank2 = getMovieGenreRank(m2, cleanSelectedGenresSet, currentCountsForSorting);
                return Integer.compare(rank2, rank1);
            });
        }

        view.showMovies(displayedMovies);
        view.showLoadCorrect(displayedMovies.size());
    }

    /**
     * Filtra una lista de películas según los géneros seleccionados.
     * @param movies La lista de películas a filtrar.
     * @return Una nueva lista con las películas que cumplen el criterio de género.
     */
    private List<Movie> applyGenreFilter(List<Movie> movies) {
        if (selectedGenresForFilter == null || selectedGenresForFilter.isEmpty()) {
            return movies;
        }

        Set<String> cleanSelectedGenres = selectedGenresForFilter.stream()
                .map(name -> name.replaceAll("\\s*\\(\\d+\\)$", "").trim())
                .collect(Collectors.toSet());

        return movies.stream()
                .filter(movie -> {
                    if (movie.getGenres() == null || movie.getGenres().isEmpty()) {
                        return cleanSelectedGenres.contains("NA");
                    } else {
                        return movie.getGenres().stream()
                                .anyMatch(genre -> cleanSelectedGenres.contains(genre.getName()));
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * Filtra una lista de películas según las décadas seleccionadas.
     * @param movies La lista de películas a filtrar.
     * @return Una nueva lista con las películas que cumplen el criterio de década.
     */
    private List<Movie> applyDecadeFilter(List<Movie> movies) {
        if (selectedDecadesForFilter == null || selectedDecadesForFilter.isEmpty()) {
            return movies;
        }

        List<String> cleanSelectedDecades = selectedDecadesForFilter.stream()
                .map(decade -> decade.replaceAll("\\s*\\(\\d+\\)$", "").trim())
                .collect(Collectors.toList());

        return movies.stream().filter(movie -> {
            String yearStr = movie.getYear();
            if (yearStr == null || yearStr.trim().isEmpty()) return false;
            try {
                int year = Integer.parseInt(yearStr.trim());
                String decadeString = (year / 10) * 10 + "'s";
                return cleanSelectedDecades.contains(decadeString);
            } catch (NumberFormatException e) {
                return false;
            }
        }).collect(Collectors.toList());
    }

    /**
     * Calcula un "rango" o "puntuación" para una película, basado en la popularidad de sus géneros
     * dentro de la lista de géneros seleccionados. Se usa para ordenar las películas.
     * @param movie La película a la que calcular el rango.
     * @param selectedGenres Los géneros seleccionados por el usuario.
     * @param counts El mapa con los conteos de popularidad de cada género.
     * @return El rango máximo encontrado entre los géneros de la película.
     */
    private int getMovieGenreRank(Movie movie, Set<String> selectedGenres, Map<String, Integer> counts) {
        if (movie.getGenres() == null || movie.getGenres().isEmpty()) {
            return selectedGenres.contains("NA") ? counts.getOrDefault("NA", 0) : 0;
        }
        int maxRank = 0;
        for (Genres genre : movie.getGenres()) {
            if (selectedGenres.contains(genre.getName())) {
                maxRank = Math.max(maxRank, counts.getOrDefault(genre.getName(), 0));
            }
        }
        return maxRank;
    }

    /**
     * Maneja el evento de clic sobre una película de la lista.
     * @param movie La película seleccionada.
     */
    @Override
    public void onItemClicked(Movie movie) {
        if (movie != null) {
            view.showMovieDetails(movie);
        }
    }

    /**
     * Maneja el evento de clic sobre el botón de información del menú.
     */
    @Override
    public void onMenuInfoClicked() {
        view.showInfoActivity();
    }
}
