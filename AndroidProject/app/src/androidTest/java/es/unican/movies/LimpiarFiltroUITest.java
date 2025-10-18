package es.unican.movies;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


// Útil para asegurar visibilidad
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
import es.unican.movies.activities.main.MainView;
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


    private static final String TITULO_PELI_SIN_FILTROS = "Los Cuatro Fantásticos: Primeros pasos";
    // Titulo de la pelicula que debe desaparecer al aplicar el limpiar:
    private static final String TITULO_PELI_FILTRADA = "Universidad de Cantabria";

    /**
     * Estan seleccionados ambos filtros previamente, tanto de decada como de genero
     * Género: Suspense(4), Década: 2020-2029
     * Resultado: el desplegable se debe cerrar, la lista de películas muestra todas las películas del JSON (que son 9).

     * - Botón de filtros principal: R.id.filter_button
     * - Contenedor del desplegable/diálogo de filtros: R.id.filter_dialog_container (o un ID visible)
     * - Botón 'Limpiar' dentro del desplegable: R.id.clear_button (o con withText("Limpiar"))
     * - Lista de películas (RecyclerView): R.id.movie_list
     */

    /**
     * Simular la selección de filtros: Abrir el menú, seleccionar un Género y hacer
     * clic en Aplicar. Repetir para Década.Verificar que los filtros se aplicaron (opcional,
     * pero buena práctica). Ejecutar la acción de limpieza: Abrir el menú de filtro y seleccionar
     * Limpiar.Verificar el resultado: Comprobar que el $\text{ListView}$ se ha actualizado y
     * ahora muestra la lista completa de películas sin ningún filtro aplicado.
     */

    @Test
    public void limpiarAmbosFiltrosTest() {

        // para aplicar filtro genero - abrir menu ppal
        //openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getTargetContext());
        onView(withId(R.id.menuItemFilter)).perform(click());

        //onView(withText(R.string.filter_menu_title)).perform(click()); // click en Filtro

        // aplicar filtro de genero
      //  onView(withId(R.id.menuItemFilterGenre)).perform(click());
        onView(withText(R.string.filter_by_genre)).perform(click()); // busca por su etiqueta visible en vez del id
        onView(withText("Acción (6)")).perform(click());
        onView(withText("APLICAR")).perform(click());

        // aplicar filtro de decadas
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getTargetContext());
        onView(withText(R.string.filter_menu_title)).perform(click()); // click en Filtro
        onView(withId(R.id.menuItemFilterDecade)).perform(click());
        onView(withText("2000-2009")).perform(click());
        onView(withText("Aplicar")).perform(click());

        // se muestra peli
        onData(anything()).inAdapterView(withId(R.id.lvMovies)).check(matches(hasDescendant(withText(TITULO_PELI_FILTRADA))));

        // aplicar Limpiar
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getTargetContext());
        onView(withText(R.string.filter_menu_title)).perform(click()); // click en Filtro
        onView(withId(R.id.menuItemFilterLimpiar)).perform(click());

        // usar vista para verificar que la lista se ha modificado, salen mas peliculas
        // o un elemento que cumplia los filtros ahora NO esta visible

        onData(anything()).inAdapterView(withId(R.id.lvMovies)).check(matches(isDisplayed()));
        onData(anything())
                .inAdapterView(withId(R.id.lvMovies))
                .check(matches(hasDescendant(withText(TITULO_PELI_SIN_FILTROS))));


        //onView(allOf(withText("Universidad de Cantabria"), isDisplayed())).check(matches(isDisplayed()));
        // O, si la implementación usa un contador:
        // onView(withId(R.id.movie_list_view))
        //     .check(matches(hasChildCount(50))) // Verifica que se restauró el tamaño completo
    }


    @Test
    public void limpiaSinFiltrosSeleccionados() {

        // no hay filtros seleccionados (lista completa de pelis visibles)
        // asumo que peli x visible desde el primer momento
        onData(anything())
                .inAdapterView(withId(R.id.lvMovies))
                .check(matches(hasDescendant(withText(TITULO_PELI_SIN_FILTROS))));

        // pulsar limpiar
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getTargetContext());
        onView(withText(R.string.filter_menu_title)).perform(click()); // click en Filtro
        onView(withId(R.id.menuItemFilterLimpiar)).perform(click());

        // verificar resultado, la lista sigue siendo la misma y el elemento sigue mostrado
        onData(anything())
                .inAdapterView(withId(R.id.lvMovies))
                .check(matches(hasDescendant(withText(TITULO_PELI_SIN_FILTROS))));

    }

}