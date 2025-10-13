package es.unican.movies;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.anything;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import es.unican.movies.activities.details.DetailsView;
import es.unican.movies.activities.main.MainView;

@RunWith(AndroidJUnit4.class)
public class casoExitoTest {


    @Rule
    public ActivityScenarioRule<MainView> activityRule = new ActivityScenarioRule<>(MainView.class);


    @Test
    public void peliMuestraDetallesTest(){
        // datos de la 1º peli del listView
        final String TITULO_ESPERADO = "The Fantastic 4: First Steps";
        final String ANIO_ESPERADO = "2025";
        final String DURACION_ESPERADA = "115";
        final String GENERO_ESPERADO = "Ciencia ficción, Aventura";
        final String PUNTUACION_ESPERADA = "7.175";
        final String PSUMARIA_ESPERADA = "calculadaPorMetodo";

        // Accede o clicka en el primer elemento de la lista
        // usa onData pq no todos los objetos son visibles y cargados de primeras
        onData(anything()).inAdapterView(withId(R.id.lvMovies)).atPosition(0).perform(click());

        // Verifica que la vistaDetalle está visible
        onView(ViewMatchers.withId(R.id.tvTitle)).check(matches(isDisplayed()));

        onView(withId(R.id.tvTitle)).check(matches(withText(TITULO_ESPERADO)));
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
        onData(anything()).inAdapterView(withId(R.id.lvMovies)).atPosition(11).perform(click());
        onView(withId(R.id.tvTitle)).check(matches(isDisplayed()));
    }


    @Test
    public void lanzaActivityTest (){
        onData(anything()).inAdapterView(withId(R.id.lvMovies)).atPosition(0).perform(click());
        onView(ViewMatchers.withId(R.id.tvGenero)).check(matches(isDisplayed())); // Verifica que la vista está visible
        intented(hasComponent(DetailsView.class.getName())); // Verifica que lanza la actividad DetailsView)
    }
}
