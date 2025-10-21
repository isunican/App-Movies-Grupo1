package es.unican.movies.activities.main;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.not;

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
    private static final int NUM_TOTAL_PELIS = 4; // 11 en total en adaptes, 4 en pantalla

    /**
     * Simular la selección de los 2 filtros, verificar que los filtros se aplicaron.
     * Ejecutar limpiar: Abrir el menú de filtro y seleccionar
     * Verificar el resultado: Comprobar que el $\text{ListView}$ se ha actualizado y
     * ahora muestra la lista completa de películas sin ningún filtro aplicado.
     */

    @Test
    public void limpiarAmbosFiltrosTest() {

        // para aplicar filtros - abrir menu ppal
        onView(withId(R.id.menuItemFilter)).perform(click());

        // seleccionar filtro 'Género'
        onView(withText(R.string.filter_by_genre)).perform(click()); // busca por su etiqueta visible en vez del id
        onView(withText("Terror (3)")).perform(click());
        onView(withText("APLICAR")).perform(click());

        // aplicar filtro de 'Década'
        onView(withId(R.id.menuItemFilter)).perform(click());  // abre icono filtros
        onView(withText(R.string.filter_by_decade)).perform(click()); // clicka en 'Década'

        onView(withText("2000's (1)")).perform(scrollTo(), click());
        onView(withText("APLICAR")).perform(click());


        // usar vista para verificar que la lista se ha modificado, sale una peli concreta que no
        // aparecia en la lista principal a primera vista
        onData(anything()).inAdapterView(withId(R.id.lvMovies))
                .check(matches(hasDescendant(withText(TITULO_PELI_FILTRADA))));
        // verificacion de que solo aparece una pelicula que cumple los filtros
        onView(withId(R.id.lvMovies)).check(matches(hasChildCount(1)));


        // CLICK EN LIMPIAR
        onView(withId(R.id.menuItemFilter)).perform(click()); // click en filtros
        onView(withText(R.string.filter_limpiar)).perform(click()); // click en limpiar


        /**try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }*/

        // comprueba que ya no se muestra la pelicula que aparecia tras aplicar los filtros
        onData(anything())
                .inAdapterView(withId(R.id.lvMovies))
                .check(matches(not(hasDescendant(withText(TITULO_PELI_FILTRADA)))));

        onView(withText(TITULO_PELI_FILTRADA)).check(matches(not(isDisplayed())));


        // La película SIN FILTROS (que antes no cumplía el filtro) AHORA es visible
        onView(withText(TITULO_PELI_SIN_FILTROS)).check(matches(isDisplayed()));


        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Ademas, se prueba que se muestra el numero total de peliculas inicial (el total).
        onView(withId(R.id.lvMovies))
             .check(matches(hasChildCount(NUM_TOTAL_PELIS))); // numPelis en pantalla (4)
    }


    @Test
    public void limpiaSinFiltrosSeleccionados() {

        // no hay filtros seleccionados (lista completa de pelis visibles)
        onData(anything())
                .inAdapterView(withId(R.id.lvMovies))
                .check(matches(hasDescendant(withText(TITULO_PELI_SIN_FILTROS))));

        onView(withId(R.id.menuItemFilter)).perform(click()); // pulsa en filtros
        onView(withText(R.string.filter_limpiar)).perform(click()); // pulsa limpiar

        // verificar resultado, la lista sigue siendo la misma y el elemento sigue mostrado
        onData(anything())
                .inAdapterView(withId(R.id.lvMovies))
                .check(matches(hasDescendant(withText(TITULO_PELI_SIN_FILTROS))));

        // verifica que la peli que necesita filtrado no aparece
        onData(anything())
                .inAdapterView(withId(R.id.lvMovies))
                .check(matches(not(hasDescendant(withText(TITULO_PELI_FILTRADA)))));

        // verifica que se muestra el numero total de peliculas
        onView(withId(R.id.lvMovies))
                .check(matches(hasChildCount(NUM_TOTAL_PELIS)));
    }



}