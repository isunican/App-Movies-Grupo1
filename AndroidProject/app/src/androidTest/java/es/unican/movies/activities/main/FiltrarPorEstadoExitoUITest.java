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
import static es.unican.movies.utils.Matchers.peliculasenPendientesDeVer3;
import static es.unican.movies.utils.MockRepositories.getTestRepository;
import es.unican.movies.utils.Matchers;
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
public class FiltrarPorEstadoExitoUITest {
    @Rule(order = 0)  // the Hilt rule must execute first
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<MainView> activityRule = new ActivityScenarioRule<>(MainView.class);

    // I need the context to access resources, such as the json with movies
    final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
/*
    @Test
    public void FiltrarEstadoExitoso() throws InterruptedException {
        //Paso0:Añado 4 películas a la lista (3 pendientes y 1 finalizada)

            // Espera que la lista de peliculas se cargue
            Thread.sleep(3000);

            // Click en el botón "Añadir" del primer elemento
            onData(anything())
                    .inAdapterView(withId(R.id.lvMovies))
                    .atPosition(0)
                    .onChildView(withId(R.id.btnAnadir))
                    .perform(click());

            // Abrir el spinner de "Estado"
            onView(withId(R.id.spinnerEstado)).perform(click());

            // Seleccionar "Pendiente" del popup del Spinner
            onData(anything())
                    .inRoot(isPlatformPopup())
//---------------------------------------------------
        //Pelicula2

            // Espera que la lista de peliculas se cargue
            Thread.sleep(3000);

            // Click en el botón "Añadir" del primer elemento
            onData(anything())
                    .inAdapterView(withId(R.id.lvMovies))
                    .atPosition(1)
                    .onChildView(withId(R.id.btnAnadir))
                    .perform(click());

            // Abrir el spinner de "Estado"
            onView(withId(R.id.spinnerEstado)).perform(click());

            // Seleccionar "En Progreso" del popup del Spinner
            onData(anything())
                    .inRoot(isPlatformPopup())
        //Pelícla3

            // Espera que la lista de peliculas se cargue
            Thread.sleep(3000);

            // Click en el botón "Añadir" del primer elemento
            onData(anything())
                    .inAdapterView(withId(R.id.lvMovies))
                    .atPosition(2)
                    .onChildView(withId(R.id.btnAnadir))
                    .perform(click());

            // Abrir el spinner de "Estado"
            onView(withId(R.id.spinnerEstado)).perform(click());

            // Seleccionar "En Progreso" del popup del Spinner
            onData(anything())
                    .inRoot(isPlatformPopup())
        //Pelicula4

            // Espera que la lista de peliculas se cargue
            Thread.sleep(3000);

            // Click en el botón "Añadir" del primer elemento
            onData(anything())
                    .inAdapterView(withId(R.id.lvMovies))
                    .atPosition(3)
                    .onChildView(withId(R.id.btnAnadir))
                    .perform(click());

            // Abrir el spinner de "Estado"
            onView(withId(R.id.spinnerEstado)).perform(click());

            // Seleccionar "En Progreso" del popup del Spinner
            onData(anything())
                    .inRoot(isPlatformPopup())
        // Paso 1: El usuario va a la pantalla de la lista
        onView(withId(R.id.btn_Lista))
                .perform(click());
        // Paso2: Compruebo que se pasa a la activity de la lisat del usuario
        intended(hasComponent(UserListVIew.class.getName()));
        // Paso 3:Despliega el menu de filtros
        onView(withId(R.id.menuItemFilter))
                .perform(click());

        // Paso 4: El sistema muestra el desplegable con los filtros posibles
        onView(withText("Estado"))
                .check(matches(isDisplayed()));

        // Paso 5: El usuario selecciona el filtro de "Estado"
        onView(withId(R.id.munuItemFilterEstado))
                .perform(click());

        // Paso 6: Pulsar el botón "Éxito" en el desplegable
        onView(withId(R.id.btnEstadoPendiente))
                .perform(click());

        // Paso 7: Pulsar el botón "Aplicar" para aplicar el filtro
        onView(withId(R.id.btnAplicarEstado))
                .perform(click());
        // Paso 8: Verificar que vista tiene las 3 peliculas pendientes
        onView(withId(R.id.UserListVIew)).check(matches(peliculasenPendientesDeVer3()));

    }

 */
}
