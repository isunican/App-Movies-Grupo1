package es.unican.movies.activities.main;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isNotEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static es.unican.movies.utils.MockRepositories.getTestRepository;

import android.content.Context;
import android.widget.ListView;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;
import es.unican.movies.R;
import es.unican.movies.injection.RepositoriesModule;
import es.unican.movies.model.Movie;
import es.unican.movies.service.IMoviesRepository;

@UninstallModules(RepositoriesModule.class)
@HiltAndroidTest
public class FiltrarPorDecadaUITest {

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
    public void filtrarDecadaExitoso() {

        // Pulsamos el boton y aplicamos el filtro por década de 2020
        onView(withId(R.id.menuItemFilter)).perform(click());
        onView(withId(R.id.menuItemFilterDecade)).perform(click());
        onView(withId(R.id.menuItemDecada2020)).perform(click());
        onView(withId(R.id.btnAplicarDecada)).perform(click());

        // Verificamos que aparecen las 9 películas de dicha década
        activityRule.getScenario().onActivity(activity -> {
            ListView lv = activity.findViewById(R.id.lvMovies);
            Assert.assertEquals(9, lv.getCount());

            // Verificamos que las películas sean de la década de 2020
            for (int i = 0; i < 9; i++) {
                Movie movie = (Movie) lv.getAdapter().getItem(i);
                String year = movie.getReleaseDate().substring(0, 4);
                int yearInt = Integer.parseInt(year);
                Assert.assertTrue(yearInt >= 2020 && yearInt <= 2029);
            }
        });


    }

    @Test
    public void filtrarDecadaNingunaSeleccionada() {

        // Pulsamos el boton del filtro pero no seleccionamos nada.
        onView(withId(R.id.menuItemFilter)).perform(click());
        onView(withId(R.id.menuItemFilterDecade)).perform(click());

        // Verificamos que el botón de aplicar esté deshabilitado
        onView(withId(R.id.btnAplicar)).check(matches(isNotEnabled()));
    }

}
