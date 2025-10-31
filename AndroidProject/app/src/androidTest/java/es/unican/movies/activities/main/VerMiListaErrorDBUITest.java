package es.unican.movies.activities.main;

import static androidx.test.espresso.Espresso.onView;
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

public class VerMiListaErrorDBUITest {
    @Rule
    public ActivityScenarioRule<MainView> activityRule = new ActivityScenarioRule(MainView.class);
/*
    @Test
    public void testVerMiListaExito() {
    }
*/
}
