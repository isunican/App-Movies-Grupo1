package es.unican.movies.activities.main;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.not;
import static es.unican.movies.utils.MockRepositories.getTestRepository;

import android.content.Context;
import android.widget.ListView;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.function.Predicate;

import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;
import es.unican.movies.R;
import es.unican.movies.injection.RepositoriesModule;
import es.unican.movies.model.Genres;
import es.unican.movies.model.Movie;
import es.unican.movies.service.IMoviesRepository;

@UninstallModules(RepositoriesModule.class)
@HiltAndroidTest
public class FiltrarGeneroUITest {

    @Rule(order = 0)  // the Hilt rule must execute first
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<MainView> activityRule = new ActivityScenarioRule<>(MainView.class);

    // I need the context to access resources, such as the json with movies
    final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

    // Mock repository that provides data from a JSON file instead of downloading it from the internet.
    @BindValue
    final IMoviesRepository repository = getTestRepository(context, R.raw.sample_movies_javier);

    @Test
    public void FiltrarGeneroExitoso(){
        // Paso 1: El usuario selecciona el botón de filtros
        onView(withId(R.id.menuItemFilter))
                .perform(click());

        // Paso 2: El sistema muestra el desplegable con los filtros posibles
        onView(withText("Género"))
                .check(matches(isDisplayed()));

        // Paso 3: El usuario selecciona el filtro de "Género"
        onView(withText("Género"))
                .perform(click());


        //Comprobar que sale 6 junto a "acción"
        // Paso 5: El usuario selecciona el género "Acción"
        onView(allOf(withText(containsString("Acción")), withText(containsString("6"))))
                .perform(click());

        // Paso 6: El usuario pulsa el botón "Aplicar"
        onView(withId(R.id.btnAplicarGenero))
                .perform(click());

        // Verificamos que aparecen las 6 películas de dicho género
        activityRule.getScenario().onActivity(activity -> {            ListView lv = activity.findViewById(R.id.lvMovies);
            // 1. Verificamos que el número de películas mostradas es el correcto (6)
            Assert.assertEquals(6, lv.getCount());

            // 2. Iteramos sobre las películas mostradas para verificar su género
            for (int i = 0; i < lv.getCount(); i++) { // Bucle corregido para usar lv.getCount()
                Movie movie = (Movie) lv.getAdapter().getItem(i);
                List<Genres> genres = movie.getGenres();

                // 3. Comprobamos que la película contiene el género "Acción"
                // Esto es más robusto que mirar solo la posición 0
                boolean tieneGeneroAccion = false;
                if (genres != null) {
                    for (Genres genre : genres) {
                        if ("Acción".equals(genre.getName())) {
                            tieneGeneroAccion = true;
                            break; // Salimos del bucle interior en cuanto lo encontramos
                        }
                    }
                }
                // 4. Afirmamos que el género fue encontrado para esta película
                Assert.assertTrue(movie.getTitle() , tieneGeneroAccion);
            }
        });

        // Paso 8: Verificamos que la vista de resultados está visible
        onView(withId(R.id.lvMovies))
                .check(matches(isDisplayed()));
    }
    @Test
    public void testFiltrarSinSeleccionarGenero() { // Nombre del test actualizado para describir el caso
        // Paso 1: El usuario selecciona el botón de filtros.
        onView(ViewMatchers.withId(R.id.menuItemFilter))
                .perform(click());

        // Paso 2: El sistema muestra los filtros posibles.
        onView(withText("Género"))
                .check(matches(isDisplayed()));

        // Paso 3: El usuario selecciona el filtro de "Género".
        onView(withText("Género"))
                .perform(click());
        // Paso 4: Se verifica que el sistema no permite pulsar el botón de "Aplicar".
        //Para ello, comprobamos que el botón con id `btnAplicarFiltros` NO está habilitado (isEnabled).
        onView(withId(R.id.btnAplicarGenero)).check(matches(not(isEnabled())));
        }


}
