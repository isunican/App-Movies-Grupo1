package es.unican.movies;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.robolectric.versioning.AndroidVersions;

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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
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
        MockitoAnnotations.openMocks(this);


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




/*
    @Test
    public void testCleanWithDecadeFilterApplied(){
        simulateSuccessfulLoad(); // Simula la carga inicial de 5 peliculas

        // Simulacion de que un filtro esta aplicado
        List<String> filtroAplicado = Collections.singletonList("2020's (2)");
        presenter.onDecadesFiltered(filtroAplicado);

        // Ahora solo mostraria 2 peliculas la vista
        verify(mockView, times(1)).showLoadCorrect(2);

        // Simulacion de 'Limpiar filtro' con una lista vacia
        presenter.onDecadesFiltered(new ArrayList<>());

        // Verificacion que es llamada exactamente tres veces (al cargar, aplicar el filto y tras limpiar)
        verify(mockView, times(3)).showMovies(moviesCaptor.capture());

        // Obtiene la lista de todas las listas de pelis enviadas de nuevo a la vista (que son 3)
        List<List<Movie>> peliculasCapturadas = moviesCaptor.getAllValues();
        Assert.assertEquals(5, peliculasCapturadas.get(0).size()); // tras carga inicial (las 5)
        Assert.assertEquals(2, peliculasCapturadas.get(1).size()); // tras aplicar filtro (solo 2)
        Assert.assertEquals(5, peliculasCapturadas.get(2).size()); // vuelve a 5

        List<Movie> listaPelisTrasLimpiar = peliculasCapturadas.get(2);

        // La lista contiene todas las pelis disponibles
        Assert.assertEquals(allMovies.size(), listaPelisTrasLimpiar.size());
        Assert.assertTrue(listaPelisTrasLimpiar.containsAll(allMovies));

        // No hubo errores de carga
        verify(mockView, never()).showLoadError();

        // Se le llamo 2 veces con el recuento correcto (al aplicar filtro y tras limpiar)
        verify(mockView, times(2)).showLoadCorrect(5);
    }

    @Test
    public void testCleanWithGenreFilterApplied(){
        simulateSuccessfulLoad(); // Simula la carga inicial de 5 peliculas

        // Simulacion de que un filtro esta aplicado
        List<String> filtroAplicado = Collections.singletonList("Acción (2)");
        presenter.onGenresFiltered(filtroAplicado);

        // Ahora solo mostraria 2 peliculas la vista (movie1, movie2)
        verify(mockView, times(1)).showLoadCorrect(2);

        // Simulacion de 'Limpiar filtro' con una lista vacia
        presenter.onGenresFiltered(new ArrayList<>());

        // Verificacion que es llamada exactamente tres veces (al cargar, aplicar el filto y tras limpiar)
        verify(mockView, times(3)).showMovies(moviesCaptor.capture());

        // Obtiene la lista de todas las listas de pelis enviadas de nuevo a la vista (que son 3)
        List<List<Movie>> peliculasCapturadas = moviesCaptor.getAllValues();
        Assert.assertEquals(5, peliculasCapturadas.get(0).size()); // tras carga inicial (las 5)
        Assert.assertEquals(2, peliculasCapturadas.get(1).size()); // tras aplicar filtro (solo 2)
        Assert.assertEquals(5, peliculasCapturadas.get(2).size()); // vuelve a 5

        List<Movie> listaPelisTrasLimpiar = peliculasCapturadas.get(2);

        // La lista contiene todas las pelis disponibles
        Assert.assertEquals(allMovies.size(), listaPelisTrasLimpiar.size());
        Assert.assertTrue(listaPelisTrasLimpiar.containsAll(allMovies));

        // No hubo errores de carga
        verify(mockView, never()).showLoadError();

        // Se le llamo 2 veces con el recuento correcto (al aplicar filtro y tras limpiar)
        verify(mockView, times(2)).showLoadCorrect(5);
    }

    @Test
    public void testCleanWithBothFiltersApplied(){
        simulateSuccessfulLoad(); // primera llamada a showLoadCorrect(5)

        List<String> filtroGeneroAplicado = Collections.singletonList("Aventura (1)");  // segunda con showLoadCorrect(1)
        List<String> filtroDecadaAplicado = Collections.singletonList("1990's (1)"); // tercera con showLoadCorrect(1)

        // Aplicar los filtros (2x applyFilters)
        presenter.onGenresFiltered(filtroGeneroAplicado);
        presenter.onDecadesFiltered(filtroDecadaAplicado);

        verify(mockView, times(2)).showLoadCorrect(1);

        // Simular limpiar filtro
        presenter.onGenresFiltered(new ArrayList<>());
        presenter.onDecadesFiltered(new ArrayList<>());

        // showMovies fue llamado 5 veces en total (1 carga + 2 aplicar + 2 limpiar)
        verify(mockView, times(5)).showMovies(moviesCaptor.capture());

        // Obtiene la lista de todas las listas de pelis enviadas de nuevo a la vista (que son 5)
        List<List<Movie>> peliculasCapturadas = moviesCaptor.getAllValues();

        Assert.assertEquals(5, peliculasCapturadas.get(0).size());  // carga (5)
        Assert.assertEquals(1, peliculasCapturadas.get(1).size()); // aplico genero (1)
        Assert.assertEquals(1, peliculasCapturadas.get(2).size()); // and decada (1)
        Assert.assertEquals(1, peliculasCapturadas.get(3).size());  // limpio genero (1)
        Assert.assertEquals(5, peliculasCapturadas.get(4).size());  // limpio decada (1)

        // La última lista debe ser la restauración completa
        List<Movie> listaPelisTrasLimpiar = peliculasCapturadas.get(4);

        Assert.assertEquals(allMovies.size(), listaPelisTrasLimpiar.size());
        Assert.assertTrue(listaPelisTrasLimpiar.containsAll(allMovies));

        // Verificar que showLoadCorrect(5) fue llamado 2 veces (carga, tras limpieza)
        verify(mockView, times(2)).showLoadCorrect(5);
    }

    @Test
    public void testCleanWithNoFiltersApplied() {
        simulateSuccessfulLoad();

        // Simulacion limpiar filtro
        presenter.onDecadesFiltered(new ArrayList<>());

        // dos veces en carga y limpiar
        verify(mockView, times(2)).showMovies(moviesCaptor.capture());

        // Obtiene la lista de todas las listas de pelis enviadas de nuevo a la vista (que son 2)
        List<List<Movie>> peliculasCapturadas = moviesCaptor.getAllValues();

        Assert.assertEquals(5, peliculasCapturadas.get(0).size());  // carga (5)
        Assert.assertEquals(5, peliculasCapturadas.get(1).size()); // tras limpiar (5)

        // Deben ser iguales
        List<Movie> listaPelisTrasLimpiar = peliculasCapturadas.get(1);
        Assert.assertEquals(allMovies.size(), listaPelisTrasLimpiar.size());

        verify(mockView, never()).showLoadError();
        verify(mockView, times(2)).showLoadCorrect(5);

    }


    @Test
    public void testLoadFails(){
        presenter = new MainPresenter();
        when(mockView.getMoviesRepository()).thenReturn(mockRepo);

        // simular que requestAggregateMovies() falla
        // simula error en API y ejecuta onFailure del callback
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                // Capturamos el ICallback
                ICallback<List<Movie>> theCallback = invocation.getArgument(0);
                theCallback.onFailure(new Throwable("Error de conexion simulado"));
                return null; // request es void
            }
        }).when(mockRepo).requestAggregateMovies(any());

        // 2. Inicializar el presenter, que dispara el load() fallido
        presenter.init(mockView);

        // allMovies es nulo(falla de carga), por lo que showMovies NO es llammado
        verify(mockView, never()).showMovies(any());
        verify(mockView, never()).showLoadCorrect(any(Integer.class));

        // la vista debe ser notificada
        verify(mockView, times(1)).showLoadError();
    }
*/
}
