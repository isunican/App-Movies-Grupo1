package es.unican.movies.utils;

import android.view.View;
import android.widget.ListView;

import androidx.recyclerview.widget.RecyclerView;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Custom matchers for UI tests.
 */
public class Matchers {

    public static Matcher<View> peliculasenPendientesDeVer3() {
        return new TypeSafeMatcher<View>() {
            @Override
            public boolean matchesSafely (final View view) {
                return ((ListView) view).getCount () == 3;
            }
            @Override
            public void describeTo (final Description description) {
                description.appendText ("ListView debe tener 3 películas en estado PENDIENTE");
            }
        };
    }

    public static Matcher<View> isEmpty() {
        return new TypeSafeMatcher<View>() {
            @Override
            public boolean matchesSafely (final View view) {
                return ((ListView) view).getCount () == 0;
            }
            @Override
            public void describeTo (final Description description) {
                description.appendText ("La lista de películas está vacia");
            }
        };
    }

}
