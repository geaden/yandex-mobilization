package com.geaden.android.mobilization.app.artists;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.geaden.android.mobilization.app.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Tests for main screen with artists grid.
 *
 * @author Gennady Denisov
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ArtistsActivityScreen {
    /**
     * {@link ActivityTestRule} to be executed for each test method.
     */
    @Rule
    public ActivityTestRule<ArtistsActivity> mArtistsActivityRule =
            new ActivityTestRule<>(ArtistsActivity.class);

    @Test
    public void showEmptyArtistsList() {
        // Shows empty artists...
        onView(withId(R.id.empty)).check(matches(isDisplayed()));
    }

}