package es.unican.movies.activities.main;

import static androidx.test.espresso.Espresso.*;
import androidx.test.ext.junit.rules.ActivityScenarioRule;


import org.junit.Rule;
import org.junit.Test;


public class VerMiListaExitoUITest {
    private static final Object NUM_PELIS_VISIBLES = 6; //TODO: Cambiar numero de peliculas

    @Rule
    public ActivityScenarioRule<MainView> activityRule = new ActivityScenarioRule(MainView.class);

    @Test
    public void testVerMiListaExito() {
        //Pulsar el botón "Mi lista"
        onView(withId(R.id.menuItemMiLista)).perform(click()); //TODO: Cambiar id boton

        //1. Verificar que se abre la nueva vista

        //1.1 Verificar que se mantiene la barra de herramientas
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()));

        //1.2 Verificar que se muestra el encabezado
        onView(withId(R.id.tvEncabezado)).check(matches(isDisplayed())); //TODO: Cambiar id encabezado
        //Verificar que al hacer scroll down el encabezado se mantiene fijo
        onView(withId(R.id.lvMovies)).perform(swipeUp());
        onView(withId(R.id.tvEncabezado)).check(matches(isDisplayed()));

        //1.3 Verificar que se muestra la lista de películas
        onView(withId(R.id.lvMovies)).check(matches(isDisplayed()));

        //Verificar que se ven todas las peliculas que hay en el estado inicial
        onView(withId(R.id.lvMovies)).check(matches(hasChildCount(NUM_PELIS_VISIBLES)));
    }
}
