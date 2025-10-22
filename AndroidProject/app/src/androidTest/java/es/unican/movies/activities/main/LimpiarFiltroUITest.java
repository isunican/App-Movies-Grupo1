package es.unican.movies.activities.main;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;

import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static org.hamcrest.Matchers.anything;

import static es.unican.movies.utils.MockRepositories.getTestRepository;
import android.content.Context;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Rule;
import org.junit.Test;

import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;
import es.unican.movies.R;
import es.unican.movies.injection.RepositoriesModule;
import es.unican.movies.service.IMoviesRepository;


@UninstallModules(RepositoriesModule.class)
@HiltAndroidTest
public class LimpiarFiltroUITest {

    @Rule(order = 0)  // the Hilt rule must execute first
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<MainView> activityRule = new ActivityScenarioRule<>(MainView.class);

    // I need the context to access resources, such as the json with movies
    final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

    // Mock repository that provides data from a JSON file instead of downloading it from the internet.
    @BindValue
    final IMoviesRepository repository = getTestRepository(context, R.raw.sample_movies_estela);


    private static final String TITULO_PELI_SIN_FILTROS = "The Fantastic 4: First Steps";
    private static final String TITULO_PELI_FILTRADA = "Universidad de Cantabria";
    private static final int NUM_PELIS_VISIBLES_EN_PANTALLA = 4; // Android 10-pixel2- api 29


    @Test
    public void limpiarAmbosFiltrosTest() {

        // Para poder aplicar los filtros, abrir menu ppal
        onView(withId(R.id.menuItemFilter)).perform(click());

        // Seleccionar y aplicar el filtro 'Género'
        onView(withText(R.string.filter_by_genre)).perform(click()); // busca por su etiqueta visible
        onView(withText("Terror (3)")).perform(click());
        onView(withText("APLICAR")).perform(click());

        // Seleccionar y aplicar filtro de 'Década'
        onView(withId(R.id.menuItemFilter)).perform(click());  // abre icono filtros
        onView(withText(R.string.filter_by_decade)).perform(click()); // click en 'Década'
        onView(withText("2000's (1)")).perform(scrollTo(), click());
        onView(withText("APLICAR")).perform(click());

        // Verificacion del filtrado aplicado
        // Peliculas visibles modificadas, se muestra una pelicula solo visible tras uso de filtros
        onData(anything()).inAdapterView(withId(R.id.lvMovies))
                .check(matches(hasDescendant(withText(TITULO_PELI_FILTRADA))));
        // Verificacion de que solo existe y aparece una pelicula que cumple los filtros
        onView(withId(R.id.lvMovies)).check(matches(hasChildCount(1)));


        // Seleccion y aplicacion de Limpiar
        onView(withId(R.id.menuItemFilter)).perform(click());
        onView(withText(R.string.filter_limpiar)).perform(click());

        // Verificar que se ha cerrado la vista del menu
        onView(withText(R.string.filter_limpiar)).check(doesNotExist());


        // Tras limpiar, vuelve a aparecer la primera pelicula visible sin aplicar filtros
        onData(anything()).inAdapterView(withId(R.id.lvMovies)).atPosition(0).
                onChildView(withId(R.id.tvTituloGenero)).check(matches(withText(TITULO_PELI_SIN_FILTROS)));

        // Ya no se muestra la peli que daba resultado de los filtros
        onView(withText(TITULO_PELI_FILTRADA)).check(doesNotExist());

        // La lista se restaura al tamanho total para el movil pixel 2
        onView(withId(R.id.lvMovies))
                .check(matches(hasChildCount(NUM_PELIS_VISIBLES_EN_PANTALLA)));

    }


    @Test
    public void limpiaSinFiltrosSeleccionados() {

        // Verifica que no hay filtros aplicados, esta visible la primera pelicula por defecto
        onData(anything()).inAdapterView(withId(R.id.lvMovies)).atPosition(0).
                onChildView(withId(R.id.tvTituloGenero)).check(matches(withText(TITULO_PELI_SIN_FILTROS)));


        // Seleccionar y aplicar Limpiar
        onView(withId(R.id.menuItemFilter)).perform(click());
        onView(withText(R.string.filter_limpiar)).perform(click());

        // Verificar que se ha cerrado la vista del menu
        onView(withText(R.string.filter_limpiar)).check(doesNotExist());

        // Verificar que sigue visible la primera pelicula por defecto
        onData(anything()).inAdapterView(withId(R.id.lvMovies)).atPosition(0).
                onChildView(withId(R.id.tvTituloGenero)).check(matches(withText(TITULO_PELI_SIN_FILTROS)));

    }



}