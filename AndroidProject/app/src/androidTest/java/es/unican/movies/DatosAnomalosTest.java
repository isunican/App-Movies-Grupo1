package es.unican.movies;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;

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
public class DatosAnomalosTest {
    @Rule(order = 0)  // the Hilt rule must execute first
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<MainView> activityRule = new ActivityScenarioRule<>(MainView.class);

    // I need the context to access resources, such as the json with movies
    final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

    // Mock repository that provides data from a JSON file instead of downloading it from the internet.
    @BindValue
    final IMoviesRepository repository = getTestRepository(context, R.raw.sample_movies_sergio);

    @Test
    public void movieList_exists() {
        onView(withId(R.id.lvMovies)).check(matches(isDisplayed()));
        onData(anything()).inAdapterView(withId(R.id.lvMovies)).atPosition(0).check(matches(isDisplayed()));
    }

    @Test
    public void test3() {
        // Esta película tiene un dato anómalo
        String peliculaDatoAnomalo = "The Fantastic 4: First Steps";

        // 1. Hago click en el primer elemento de la lista, que tiene datos anómalos.
        onData(anything()).inAdapterView(withId(R.id.lvMovies)).atPosition(0).perform(click());

        // 2. Verifico que se ha cambiado a la vista de información detallada de la película correcta y el título es correcto.
        onView(withId(R.id.tvTituloGenero)).check(matches(withText(peliculaDatoAnomalo)));

        // 3. Verifico que el resto de campos válidos de la película se muestran correctamente.
        onView(withId(R.id.tvGenero)).check(matches(withText("Ciencia ficción, Aventura")));
        onView(withId(R.id.tvDuracion)).check(matches(withText("1h 55m")));
        onView(withId(R.id.tvEstreno)).check(matches(withText("2025")));
        onView(withId(R.id.tvPuntuacionMedia)).check(matches(withText("7.18")));
        onView(withId(R.id.imPoster)).check(matches(isDisplayed()));

        // 4. Verifico que la puntuación sumaria es "-" (debe ser así puesto que vote_count == -15).
        onView(withId(R.id.tvPuntuacionSumaria)).check(matches(withText("-")));
    }
}