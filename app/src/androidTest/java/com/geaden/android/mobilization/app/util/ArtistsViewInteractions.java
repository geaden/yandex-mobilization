package com.geaden.android.mobilization.app.util;

import android.support.test.espresso.ViewInteraction;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

/**
 * Custom view interactions.
 *
 * @author Gennady Denisov
 */
public class ArtistsViewInteractions {

    public static ViewInteraction matchToolbarTitle(CharSequence title) {
        return onView(allOf(isAssignableFrom(TextView.class),
                withParent(isAssignableFrom(Toolbar.class)))).check(matches(withText(title.toString())));
    }
}
