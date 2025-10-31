package es.unican.movies.activities.main;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;


import org.junit.Rule;
import org.junit.Test;

import es.unican.movies.R;


public class VerMiListaExitoUITest {
    private static final int NUM_PELIS_VISIBLES = 6; //TODO: Cambiar numero de peliculas

    @Rule
    public ActivityScenarioRule<MainView> activityRule = new ActivityScenarioRule(MainView.class);
/*
    @Test
    public void testVerMiListaExito() {
        //Pulsar el botón "Mi lista"
        onView(withId(R.id.btn_lista)).perform(click());
        //1. Verificar que se abre la nueva vista

        //1.1 Verificar que se mantiene la barra de herramientas
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()));

        //1.2 Verificar que se muestra el encabezado
        onView(withId(R.id.tvTitulo)).check(matches(isDisplayed()));
        onView(withId(R.id.tvEstado)).check(matches(isDisplayed()));
        onView(withId(R.id.tvValoracion)).check(matches(isDisplayed()));


        //Verificar que al hacer scroll down el encabezado se mantiene fijo
        onView(withId(R.id.lvMiLista)).perform(swipeUp());
        onView(withId(R.id.tvTitulo)).check(matches(isDisplayed()));
        onView(withId(R.id.tvEstado)).check(matches(isDisplayed()));
        onView(withId(R.id.tvValoracion)).check(matches(isDisplayed()));

        //1.3 Verificar que se muestra la lista de películas
        onView(withId(R.id.lvMiLista)).check(matches(isDisplayed()));

        //Verificar que se ven todas las peliculas que hay en el estado inicial
        onView(withId(R.id.lvMiLista)).check(matches(hasChildCount(NUM_PELIS_VISIBLES)));
    }
    */
}
