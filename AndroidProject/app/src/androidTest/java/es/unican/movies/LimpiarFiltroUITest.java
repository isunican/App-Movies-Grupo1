package es.unican.movies;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static es.unican.movies.utils.MockRepositories.getTestRepository;
import android.content.Context;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Rule;
import org.junit.Test;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import static java.util.EnumSet.allOf;

import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;
import es.unican.movies.activities.main.MainView;
import es.unican.movies.injection.RepositoriesModule;
import es.unican.movies.model.Movie;
import es.unican.movies.service.IMoviesRepository;


@UninstallModules(RepositoriesModule.class)
@HiltAndroidTest
public class LimpiarFiltrosConExitoUITest {


    @Rule(order = 0)  // the Hilt rule must execute first
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<MainView> activityRule = new ActivityScenarioRule<>(MainView.class);

    // I need the context to access resources, such as the json with movies
    final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

    // Mock repository that provides data from a JSON file instead of downloading it from the internet.
    @BindValue
    final IMoviesRepository repository = getTestRepository(context, R.raw.sample_movies_estela);


    /**
     * Estan seleccionados ambos filtros previamente, tanto de decada como de genero
     * Género: Suspense(4), Década: 2020-2029
     * Resultado: el desplegable se debe cerrar, la lista de películas muestra todas las películas del JSON (que son 9).

     * - Botón de filtros principal: R.id.filter_button
     * - Contenedor del desplegable/diálogo de filtros: R.id.filter_dialog_container (o un ID visible)
     * - Botón 'Limpiar' dentro del desplegable: R.id.clear_button (o con withText("Limpiar"))
     * - Lista de películas (RecyclerView): R.id.movie_list
     */

    @Test
    public void limpiaAmbosFiltrosTest() {
        // inicialmente estan todas las pelis mostradas sin filtros
        onData(allOf(is(instanceOf(Movie.class)))).inAdapterView(withId(R.id.lvMovies)).atPosition(8).check(matches(isDisplayed()));

        // abrir o clickar sobre el boton filtros
        onView(withId(R.id.menuItemFilter)).perform(click());
        onView(withId(R.id.menuItemGenreFilter)).perform(click());

        // seleccionar filtro de genero
        onView(withText("Suspense(4)")).perform(click());
        onView(withId(R.id.btnAplicar)).perform(click());

            // se cierra este subdesplegable de generos

        // aplicar filtro de decada
        onView(withId(R.id.btnDecadas)).perform(click());
        onView(withText("2020-2029")).perform(click());
        onView(withId(R.id.btnAplicar)).perform(click());

        // CAMBIO DEL JSON PARA CAMBIAR DECADAS Y QUE SE VEA EL FILTRADO REALMENTE

        // verificar que se ha aplicado el filtro y aparecen solo las peliculas
        // con or de suspense o decadas
        onData(allOf(is(instanceOf(Movie.class))))
                .inAdapterView(withId(R.id.lvMovies))
                .atPosition(3) // Último elemento del filtro (índice 3 para 4 películas)
                .check(matches(isDisplayed()));

        // verificar que NO existe una pelicula 5, en el indice 4
        onData(allOf(is(instanceOf(Movie.class)))).inAdapterView(withId(R.id.lvMovies)).atPosition(4).check(doesNotExist());



        // volver a pinchar sobre el boton filtros y aplicar Limpiar
        onView(withId(R.id.menuItemFilter)).perform(click());
        onView(withId(R.id.menuItemClear)).perform(click());

        // verificar que se vuelven a mostrar todas las peliculas, hay metodo que diga el numero de items ??
        onData(allOf(is(instanceOf(Movie.class))))
                .inAdapterView(withId(R.id.lvMovies))
                .atPosition(8)
                .check(matches(isDisplayed()));

    }



    @Test
    public void limpiaErrorBD() {

    }

}