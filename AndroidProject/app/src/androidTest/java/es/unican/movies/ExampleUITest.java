package es.unican.movies;

import static es.unican.movies.utils.MockRepositories.getTestRepository;

import android.content.Context;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.anything;

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
public class ExampleUITest {

    @Rule(order = 0)  // the Hilt rule must execute first
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<MainView> activityRule = new ActivityScenarioRule<>(MainView.class);

    // I need the context to access resources, such as the json with movies
    final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

    // Mock repository that provides data from a JSON file instead of downloading it from the internet.
    @BindValue
    final IMoviesRepository repository = getTestRepository(context, R.raw.sample_movies_alvaro);

    @Test
    public void tituloAusenteTest() {
        onData(anything()).inAdapterView(withId(R.id.lvMovies)).atPosition(0).perform(click());
        onView(withId(R.id.tvTitle)).check(matches(withText("-")));
    }

    @Test
    public void estrenoAusenteTest() {
        onData(anything()).inAdapterView(withId(R.id.lvMovies)).atPosition(1).perform(click());
        onView(withId(R.id.tvEstreno)).check(matches(withText("-")));
    }

    @Test
    public void generoAusenteTest() {
        onData(anything()).inAdapterView(withId(R.id.lvMovies)).atPosition(3).perform(click());
        onView(withId(R.id.tvGenero)).check(matches(withText("-")));
    }

    @Test
    public void duracionAusenteTest() {
        onData(anything()).inAdapterView(withId(R.id.lvMovies)).atPosition(0).perform(click());
        onView(withId(R.id.tvDuracion)).check(matches(withText("-")));
    }

    @Test
    public void puntuacionMediaAusenteTest() {
        onData(anything()).inAdapterView(withId(R.id.lvMovies)).atPosition(0).perform(click());
        onView(withId(R.id.tvPuntuacionMedia)).check(matches(withText("-")));
    }

}
