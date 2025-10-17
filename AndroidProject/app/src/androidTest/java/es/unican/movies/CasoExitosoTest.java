package es.unican.movies;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
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
import es.unican.movies.activities.main.MainView;
import es.unican.movies.injection.RepositoriesModule;
import es.unican.movies.service.IMoviesRepository;

@UninstallModules(RepositoriesModule.class)
@HiltAndroidTest
public class CasoExitosoTest {

    @Rule(order = 0)  // the Hilt rule must execute first
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<MainView> activityRule = new ActivityScenarioRule<>(MainView.class);

    // I need the context to access resources, such as the json with movies
    final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

    // Mock repository that provides data from a JSON file instead of downloading it from the internet.
    @BindValue
    final IMoviesRepository repository = getTestRepository(context, R.raw.sample_movies_estela);

    @Test
    public void peliMuestraDetallesTest(){
        // datos de la 1º peli del listView
        final String TITULO_ESPERADO = "The Fantastic 4: First Steps";
        final String ANIO_ESPERADO = "2025";
        final String DURACION_ESPERADA = "1h 55m";
        final String GENERO_ESPERADO = "Ciencia ficción, Aventura";
        final String PUNTUACION_ESPERADA = "7.18";
        final String PSUMARIA_ESPERADA = "6.77";

        // Accede o clicka en el primer elemento de la lista
        // usa onData pq no todos los objetos son visibles y cargados de primeras
        onData(anything()).inAdapterView(withId(R.id.lvMovies)).atPosition(0).perform(click());

        // Verifica que la vistaDetalle está visible
        onView(ViewMatchers.withId(R.id.tvTituloGenero)).check(matches(isDisplayed()));

        onView(withId(R.id.tvTituloGenero)).check(matches(withText(TITULO_ESPERADO)));
        onView(withId(R.id.tvEstreno)).check(matches(withText(ANIO_ESPERADO)));
        onView(withId(R.id.tvDuracion)).check(matches(withText(DURACION_ESPERADA)));
        onView(withId(R.id.tvGenero)).check(matches(withText(GENERO_ESPERADO)));
        onView(withId(R.id.tvPuntuacionMedia)).check(matches(withText(PUNTUACION_ESPERADA)));
        onView(withId(R.id.tvPuntuacionSumaria)).check(matches(withText(PSUMARIA_ESPERADA)));

        // Verifica que se muestra imagen
        onView(withId(R.id.imPoster)).check(matches(isDisplayed()));
    }


    @Test
    public void seleccionaPeliculaConScrollTest (){
        // peli de primeras no visible, requiere scroll
        onData(anything()).inAdapterView(withId(R.id.lvMovies)).atPosition(8).perform(click());
        onView(withId(R.id.tvTituloGenero)).check(matches(isDisplayed()));
    }
}
