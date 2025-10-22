package es.unican.movies.activities.main;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import es.unican.movies.model.Genres;
import es.unican.movies.model.Movie;
import es.unican.movies.service.ICallback;
import es.unican.movies.service.IMoviesRepository;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.never;
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
        // Inicializamos objetos para probar los tests
        Genres genreAccion = new Genres();
        genreAccion.setName("Acción");
        genreAccion.setId(1);

        Genres genreAventura = new Genres();
        genreAventura.setName("Aventura");
        genreAventura.setId(2);

        Genres genreComedia = new Genres();
        genreComedia.setName("Comedia");
        genreComedia.setId(3);

        Genres genreAnomalo = new Genres();
        genreAnomalo.setName(null);
        genreAnomalo.setId(4);

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
        movie4.setGenres(Collections.singletonList(genreAventura));

        movie5 = new Movie(); // Película con año anómalo y género nulo
        movie5.setReleaseDate("bvfusd");
        movie5.setGenres(Collections.singletonList(genreAnomalo));

        allMovies = Arrays.asList(movie1, movie2, movie3, movie4, movie5);
    }

    private void simulateSuccessfulLoad() {
        presenter = new MainPresenter();
        when(mockView.getMoviesRepository()).thenReturn(mockRepo);
        presenter.init(mockView);

        // Simula la carga exitosa de datos5
        verify(mockRepo).requestAggregateMovies(callbackCaptor.capture());
        ICallback<List<Movie>> callback = callbackCaptor.getValue();
        callback.onSuccess(allMovies);
    }

    @Test
    public void testFilterByOneGenre() {
        simulateSuccessfulLoad(); // Simula que las películas ya han sido cargadas

        List<String> filter = Collections.singletonList("Acción");
        presenter.onGenresFiltered(filter); // Llamada a la función

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
        simulateSuccessfulLoad();
        List<String> filter = Arrays.asList("Acción", "Aventura");

        presenter.onGenresFiltered(filter);

        verify(mockView, times(2)).showMovies(moviesCaptor.capture());
        List<Movie> result = moviesCaptor.getValue();

        Assert.assertEquals(3, result.size());
        Assert.assertTrue(result.contains(movie1));
        Assert.assertTrue(result.contains(movie2));
        Assert.assertFalse(result.contains(movie3));
        Assert.assertTrue(result.contains(movie4));
    }

    @Test
    public void testFilterWithNoResults() {
        simulateSuccessfulLoad();
        // Filtramos por un género que no está en ninguna película
        List<String> filter = Collections.singletonList("Ciencia Ficción");

        presenter.onGenresFiltered(filter);

        verify(mockView, times(2)).showMovies(moviesCaptor.capture());
        List<Movie> result = moviesCaptor.getValue();

        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void testFilterWithEmptyMovieListGenre() {
        presenter = new MainPresenter();
        when(mockView.getMoviesRepository()).thenReturn(mockRepo);
        presenter.init(mockView);

        // Simula una carga con lista vacía
        verify(mockRepo).requestAggregateMovies(callbackCaptor.capture());
        ICallback<List<Movie>> callback = callbackCaptor.getValue();
        callback.onSuccess(new ArrayList<>()); // Lista vacía

        List<String> filter = Collections.singletonList("Acción");
        presenter.onGenresFiltered(filter);

        verify(mockView, times(2)).showMovies(moviesCaptor.capture());
        List<Movie> result = moviesCaptor.getValue();

        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void testFilterWithEmptyFilterListGenre() {
        simulateSuccessfulLoad();
        List<String> emptyFilter = new ArrayList<>();

        presenter.onGenresFiltered(emptyFilter);

        // showMovies se llama en onSuccess, y de nuevo en onGenresFiltered
        // que a su vez llama a applyFilters. Como el filtro está vacío,
        // no se vuelve a llamar a showMovies, solo la primera vez
        verify(mockView, times(2)).showMovies(moviesCaptor.capture());
        List<Movie> result = moviesCaptor.getValue();

        Assert.assertEquals(allMovies.size(), result.size());
        Assert.assertTrue(result.containsAll(allMovies));
    }

    @Test
    public void testFilterByNAGenre() {
        simulateSuccessfulLoad();
        List<String> filter = Collections.singletonList("NA");

        presenter.onGenresFiltered(filter);

        verify(mockView, times(2)).showMovies(moviesCaptor.capture());
        List<Movie> result = moviesCaptor.getValue();

        // Se visualiza la lista con las películas con fecha anómala o nula
        Assert.assertEquals(1, result.size());
        Assert.assertTrue(result.contains(movie5));
    }


    @Test
    public void testFilterWithEmptyFilterListDecade() {
        simulateSuccessfulLoad();
        List<String> emptyFilter = new ArrayList<>();

        presenter.onDecadesFiltered(emptyFilter);

        // showMovies se llama en onSuccess, y de nuevo en onDecadesFiltered
        // que a su vez llama a applyFilters. Como el filtro está vacío,
        // no se vuelve a llamar a showMovies, solo la primera vez
        verify(mockView, times(2)).showMovies(moviesCaptor.capture());
        List<Movie> result = moviesCaptor.getValue();

        // Se visualiza la lista con todas las películas disponibles
        Assert.assertEquals(allMovies.size(), result.size());
        Assert.assertTrue(result.containsAll(allMovies));
    }

    @Test
    public void testFilterBy2020sDecade() {
        simulateSuccessfulLoad();
        List<String> filter = Collections.singletonList("2020's");

        presenter.onDecadesFiltered(filter);

        verify(mockView, times(2)).showMovies(moviesCaptor.capture());
        List<Movie> result = moviesCaptor.getValue();

        // Se visualiza la lista con únicamente las películas de la década de 2020
        Assert.assertEquals(2, result.size());
        Assert.assertTrue(result.contains(movie1));
        Assert.assertTrue(result.contains(movie3));
    }

    @Test
    public void testFilterByNADecade() {
        simulateSuccessfulLoad();
        List<String> filter = Collections.singletonList("NA");

        presenter.onDecadesFiltered(filter);

        verify(mockView, times(2)).showMovies(moviesCaptor.capture());
        List<Movie> result = moviesCaptor.getValue();

        // Se visualiza la lista con las películas con fecha anómala o nula
        Assert.assertEquals(2, result.size());
        Assert.assertTrue(result.contains(movie4));
        Assert.assertTrue(result.contains(movie5));
    }

    @Test
    public void testFilterBy1920sDecade() {
        simulateSuccessfulLoad();
        List<String> filter = Collections.singletonList("1920's");

        presenter.onDecadesFiltered(filter);

        verify(mockView, times(2)).showMovies(moviesCaptor.capture());
        List<Movie> result = moviesCaptor.getValue();

        // Se visualiza la lista con únicamente las películas de la década de 1920
        Assert.assertEquals(0, result.size());
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void testFilterWithEmptyMovieListDecade() {
        presenter = new MainPresenter();
        when(mockView.getMoviesRepository()).thenReturn(mockRepo);
        presenter.init(mockView);

        // Simula una carga con lista vacía
        verify(mockRepo).requestAggregateMovies(callbackCaptor.capture());
        ICallback<List<Movie>> callback = callbackCaptor.getValue();
        callback.onSuccess(new ArrayList<>()); // Lista vacía

        List<String> filter = Collections.singletonList("2020's");
        presenter.onDecadesFiltered(filter);

        verify(mockView, times(2)).showMovies(moviesCaptor.capture());
        List<Movie> result = moviesCaptor.getValue();

        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void testClearWithFullAndEmptyList() {
        // Escenario: Probar que las listas internas se vacían correctamente
        // verificando la salida eficiente en una segunda llamada.

        // Carga
        simulateSuccessfulLoad(); // llama a showMovies(5)

        // Se anhaden elementos a selectedGenresForFilter(privado) con onGenresFiltered.
        // las listas privadas no estan llenas
        presenter.onGenresFiltered(new ArrayList<>(Collections.singletonList("Acción (2)")));

        // Limpiar 1 vez (hay filtros seleccionados)
        presenter.onLimpiarFiltroMenuClicked(); // llama a showMovies(5)

        // Verificar que se han limpiado las listas privadas (selectedGenresForFilter)
        presenter.onLimpiarFiltroMenuClicked(); // NO LLAMA a showMovies(), estan vacias


        // Se ha llamado 3 veces  (carga inicial + filtro + limpieza1)
        verify(mockView, times(3)).showMovies(any());

        // solo debe haber sido llamado 2 veces (carga + limpieza1)
        verify(mockView, times(2)).showLoadCorrect(5);

        // se llamo al aplicar el filtro
        verify(mockView, times(1)).showLoadCorrect(2);

    }

    @Test
    public void testClearWithBothFiltersApplied() {
        // Realizar carga
        simulateSuccessfulLoad();

        // Crea listas mutables (haciendolo directamente desde presenter no deja) para que
        // pueda limpiarse con .clear()
        List<String> filtroGenero = new ArrayList<>(Collections.singletonList("Acción (2)"));
        List<String> filtroDecada = new ArrayList<>(Collections.singletonList("1990's (1)"));

        // Aplicacion de filtros, llena las listas: selectedGenresForFilter, selectedDecadesForFilter
        presenter.onGenresFiltered(filtroGenero);
        presenter.onDecadesFiltered(filtroDecada);

        // verificacion
        verify(mockView, times(3)).showMovies(any());

        presenter.onLimpiarFiltroMenuClicked();

        // 4 veces: carga+ 2 aplicar filtros+ limpiar
        verify(mockView, times(4)).showMovies(moviesCaptor.capture());
        // se llama 2 veces (carga inicial + limpiar)
        verify(mockView, times(2)).showLoadCorrect(5);

        // ultima lista capturada
        List<Movie> listaRestaurada = moviesCaptor.getValue();
        Assert.assertEquals(allMovies.size(), listaRestaurada.size());

        verify(mockView, never()).showLoadError(); // verificar que no hubo errores de carga
    }




}
