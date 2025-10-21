package es.unican.movies;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import es.unican.movies.activities.main.IMainContract;
import es.unican.movies.activities.main.MainPresenter;
import es.unican.movies.model.Genres;
import es.unican.movies.model.Movie;
import es.unican.movies.service.ICallback;
import es.unican.movies.service.IMoviesRepository;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MainPresenterTest {

    private MainPresenter presenter;

    @Mock
    private IMainContract.View mockView;

    @Mock
    private IMoviesRepository mockRepo;

    @Captor
    private ArgumentCaptor<List<Movie>> moviesCaptor;

    @Captor
    private ArgumentCaptor<ICallback<List<Movie>>> callbackCaptor;

    private Movie movie1, movie2, movie3, movie4, movie5;
    private List<Movie> allMovies;

    @Before
    public void setUp() {
        // --- Configuración de datos de prueba ---
        Genres genreAccion = new Genres();
        genreAccion.setName("Acción");
        genreAccion.setId(1);

        Genres genreAventura = new Genres();
        genreAventura.setName("Aventura");
        genreAventura.setId(2);

        Genres genreComedia = new Genres();
        genreComedia.setName("Comedia");
        genreComedia.setId(3);

        movie1 = new Movie();
        movie1.setGenres(Collections.singletonList(genreAccion));
        movie1.setReleaseDate("2022"); // Película de la década de 2020

        movie2 = new Movie();
        movie2.setGenres(Arrays.asList(genreAccion, genreAventura));
        movie2.setReleaseDate("1995"); // Película de otra década

        movie3 = new Movie();
        movie3.setGenres(Collections.singletonList(genreComedia));
        movie3.setReleaseDate("2021"); // Película de la década de 2020

        movie4 = new Movie(); // Película con año nulo
        movie4.setReleaseDate(null);
        movie4.setGenres(Collections.emptyList());

        movie5 = new Movie(); // Película con año vacío
        movie5.setReleaseDate("");
        movie5.setGenres(Collections.emptyList());

        allMovies = Arrays.asList(movie1, movie2, movie3, movie4, movie5);
    }

    private void simulateSuccessfulLoad() {
        presenter = new MainPresenter();
        when(mockView.getMoviesRepository()).thenReturn(mockRepo);
        presenter.init(mockView);

        // Captura el callback y simula la carga exitosa de datos
        verify(mockRepo).requestAggregateMovies(callbackCaptor.capture());
        ICallback<List<Movie>> callback = callbackCaptor.getValue();
        callback.onSuccess(allMovies);
    }

    @Test
    public void testFilterByOneGenre() {
        // Arrange
        simulateSuccessfulLoad(); // Simula que las películas ya han sido cargadas

        // Act
        List<String> filter = Collections.singletonList("Acción");
        presenter.onGenresFiltered(filter);

        // Assert/Verify
        // Se llama a showMovies 2 veces: 1 en onSuccess, 1 después de filtrar
        verify(mockView, times(2)).showMovies(moviesCaptor.capture());
        List<Movie> result = moviesCaptor.getValue();

        Assert.assertEquals(2, result.size());
        Assert.assertTrue(result.contains(movie1));
        Assert.assertTrue(result.contains(movie2));
        Assert.assertFalse(result.contains(movie3));
    }

    @Test
    public void testFilterByMultipleGenres() {
        // Arrange
        simulateSuccessfulLoad();
        List<String> filter = Arrays.asList("Acción", "Aventura");

        // Act
        presenter.onGenresFiltered(filter);

        // Verify
        verify(mockView, times(2)).showMovies(moviesCaptor.capture());
        List<Movie> result = moviesCaptor.getValue();

        Assert.assertEquals(2, result.size());
        Assert.assertTrue(result.contains(movie1));
        Assert.assertTrue(result.contains(movie2));
        Assert.assertFalse(result.contains(movie3));
    }

    @Test
    public void testFilterWithNoResults() {
        // Arrange
        simulateSuccessfulLoad();
        // Filtramos por un género que no está en ninguna película
        List<String> filter = Collections.singletonList("Ciencia Ficción");

        // Act
        presenter.onGenresFiltered(filter);

        // Verify
        verify(mockView, times(2)).showMovies(moviesCaptor.capture());
        List<Movie> result = moviesCaptor.getValue();

        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void testFilterWithEmptyMovieList() {
        // Arrange
        presenter = new MainPresenter();
        when(mockView.getMoviesRepository()).thenReturn(mockRepo);
        presenter.init(mockView);

        // Captura el callback y simula una carga con lista vacía
        verify(mockRepo).requestAggregateMovies(callbackCaptor.capture());
        ICallback<List<Movie>> callback = callbackCaptor.getValue();
        callback.onSuccess(new ArrayList<>()); // Lista vacía

        // Act
        List<String> filter = Collections.singletonList("Acción");
        presenter.onGenresFiltered(filter);

        // Verify
        verify(mockView, times(2)).showMovies(moviesCaptor.capture());
        List<Movie> result = moviesCaptor.getValue();

        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void testFilterWithEmptyFilterList() {
        // Arrange
        simulateSuccessfulLoad();
        List<String> emptyFilter = new ArrayList<>();

        // Act
        presenter.onGenresFiltered(emptyFilter);

        // Verify
        // showMovies se llama en onSuccess, y de nuevo en onGenresFiltered
        // que a su vez llama a applyFilters. Como el filtro está vacío,
        // no se vuelve a llamar a showMovies, solo la vez inicial.
        verify(mockView, times(2)).showMovies(moviesCaptor.capture());
        List<Movie> result = moviesCaptor.getValue();

        Assert.assertEquals(allMovies.size(), result.size());
        Assert.assertTrue(result.containsAll(allMovies));
    }

    @Test
    public void testFilterNoDecadeSelected() {
        // Arrange
        simulateSuccessfulLoad();
        List<String> emptyFilter = new ArrayList<>();

        // Act
        presenter.onDecadesFiltered(emptyFilter);

        // Verify
        verify(mockView, times(2)).showMovies(moviesCaptor.capture());
        List<Movie> result = moviesCaptor.getValue();

        // Assert: Se visualiza la lista con todas las películas disponibles
        Assert.assertEquals(allMovies.size(), result.size());
        Assert.assertTrue(result.containsAll(allMovies));
    }

    @Test
    public void testFilterBy2020sDecade() {
        // Arrange
        simulateSuccessfulLoad();
        List<String> filter = Collections.singletonList("2020's");

        // Act
        presenter.onDecadesFiltered(filter);

        // Verify
        verify(mockView, times(2)).showMovies(moviesCaptor.capture());
        List<Movie> result = moviesCaptor.getValue();

        // Assert: Se visualiza la lista con únicamente las películas de la década de 2020
        Assert.assertEquals(2, result.size());
        Assert.assertTrue(result.contains(movie1));
        Assert.assertTrue(result.contains(movie3));
    }

    @Test
    public void testFilterByNA() {
        // Arrange
        simulateSuccessfulLoad();
        List<String> filter = Collections.singletonList("NA");

        // Act
        presenter.onDecadesFiltered(filter);

        // Verify
        verify(mockView, times(2)).showMovies(moviesCaptor.capture());
        List<Movie> result = moviesCaptor.getValue();

        // Assert: Se visualiza la lista con las películas con fecha anómala o nula
        Assert.assertEquals(2, result.size());
        Assert.assertTrue(result.contains(movie4));
        Assert.assertTrue(result.contains(movie5));
    }
}
