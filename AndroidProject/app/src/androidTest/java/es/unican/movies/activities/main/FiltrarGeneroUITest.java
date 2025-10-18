package es.unican.movies.activities.main;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static es.unican.movies.utils.MockRepositories.getTestRepository;

import android.content.Context;

import androidx.test.espresso.matcher.ViewMatchers;
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
        onView(withId(R.id.menuItemGenre))
                .perform(click());

        // Paso 4: El sistema muestra otro desplegable con los géneros y número de coincidencias
        // Ejemplo: "Acción (6)" debe aparecer como opción
        onView(allOf(withText(containsString("Acción")), withText(containsString("6"))))
                .check(matches(isDisplayed()));
        //Comprobar que sale 6 junto a "acción"
        // Paso 5: El usuario selecciona el género "Acción"
        onView(withText(containsString("Acción")))
                .perform(click());

        // Paso 6: El usuario pulsa el botón "Aplicar"
        onView(withId(R.id.btnAplicarFiltros))
                .perform(click());

        // Paso 7: Se verifica que el sistema muestra las películas de ese género
        for (int i = 0; i < 6; i++) { // Sabemos por el JSON que hay 6 de Acción
            onData(anything())
                    .inAdapterView(withId(R.id.recyclerPeliculas))
                    .atPosition(i)
                    .onChildView(withId(R.id.tvGenero))
                    .check(matches(withText(containsString("Acción"))));
        }

        // Paso 8: Verificamos que la vista de resultados está visible
        onView(withId(R.id.recyclerPeliculas))
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
        onView(withId(R.id.btnAplicarFiltros))
                .check(matches(isNotEnabled()));
        }


}
