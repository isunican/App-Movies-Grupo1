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

    @Override
    public void init(IMainContract.View view) {
        this.view = view;
        this.view.init();
        load();
    }

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
    @Override
    public void onFilterMenuClicked() {
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
        view.showFilterActivity(formattedGenres, selectedGenresForFilter);
    }


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

    @Override
    public void onItemClicked(Movie movie) {
        if (movie == null) {
            return;
        }
        view.showMovieDetails(movie);
    }

    @Override
    public void onMenuInfoClicked() {
        view.showInfoActivity();
    }
}
