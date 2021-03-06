package com.geaden.android.mobilization.app.artists;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.geaden.android.mobilization.app.ArtistsApplication;
import com.geaden.android.mobilization.app.R;
import com.geaden.android.mobilization.app.assertions.ArtistsViewAssertions;
import com.geaden.android.mobilization.app.di.component.DaggerTestRepositoryComponent;
import com.geaden.android.mobilization.app.di.component.RepositoryComponent;
import com.geaden.android.mobilization.app.di.modules.TestRepositoryModule;
import com.geaden.android.mobilization.app.matchers.ArtistsMatchers;
import com.geaden.android.mobilization.app.util.ArtistsViewInteractions;
import com.geaden.android.mobilization.app.util.DaggerActivityTestRule;
import com.geaden.android.mobilization.app.util.Utility;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.Is.is;

/**
 * Tests for main screen with artists grid.
 * Uses {@link CountingIdlingResource} to make sure Espresso waits for artists to be loaded from repository.
 * Example of usage of CountingIdlingResource is taken from
 * <a href="https://android.googlesource.com/platform/frameworks/testing/+/android-support-test/espresso/contrib/src/main/java/android/support/test/espresso/contrib/CountingIdlingResource.java">
 * here
 * </a>.
 *
 * @author Gennady Denisov
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class ArtistsActivityScreen {
    private static final String TAG = "ArtistsActivityScreen";
    private RepositoryComponent mTestRepositoryComponent;
    private CountingIdlingResource mIdlingResource;

    /**
     * {@link ActivityTestRule} to be executed for each test method.
     */
    @Rule
    public ActivityTestRule<ArtistsActivity> mArtistsActivityRule =
            new DaggerActivityTestRule<>(ArtistsActivity.class,
                    new DaggerActivityTestRule.OnBeforeActivityLaunchedListener<ArtistsActivity>() {

                        @Override
                        public void beforeActivityLaunched(@NonNull Application application,
                                                           @NonNull ArtistsActivity activity) {
                            mIdlingResource = new CountingIdlingResource("MockArtistsRepository");
                            mTestRepositoryComponent = DaggerTestRepositoryComponent.builder()
                                    .testRepositoryModule(new TestRepositoryModule(
                                            InstrumentationRegistry.getTargetContext(),
                                            mIdlingResource))
                                    .build();
                            ((ArtistsApplication) application).setRepositoryComponent(
                                    mTestRepositoryComponent);
                        }
                    });


    @Before
    public void registerIdlingResource() {
        // Set default order to tracks.
        Utility.setPreferredOrder(InstrumentationRegistry.getTargetContext(),
                InstrumentationRegistry.getTargetContext().getString(R.string.pref_order_by_tracks));
        Espresso.registerIdlingResources(mIdlingResource);
    }

    @Test
    public void showArtistsList() {
        // Check title
        CharSequence title = InstrumentationRegistry.getTargetContext()
                .getString(R.string.artists_title);
        ArtistsViewInteractions.matchToolbarTitle(title);

        // Check proper number of items loaded from repository.
        onView(withId(R.id.artists_list)).check(ArtistsViewAssertions.hasItemsCount(3));

        // Scroll artists list to the first displayed artists, by finding its name
        onView(withId(R.id.artists_list)).perform(
                scrollTo(hasDescendant(withText("foo"))));

        // Verify artist is displayed on a screen
        onView(ArtistsMatchers.withItemText("foo")).check(matches(isDisplayed()));
    }

    @Test
    public void searchArtistsByName() {
        onView(withId(R.id.menu_artists_search)).perform(click());

        onView(withId(android.support.design.R.id.search_src_text)).perform(typeText("foo"));

        // Check proper number of items loaded from repository.
        onView(withId(R.id.artists_list)).check(ArtistsViewAssertions.hasItemsCount(1));
    }

    @Test
    public void shouldSetOrderBy() {
        onView(withContentDescription("More options")).perform(click());
        onView(withText(R.string.artists_menu_title_order)).perform(click());
        onView(withText(R.string.artists_order_selection_title)).check(matches(isDisplayed()));
        // Check that tracks is selected.
        onView(withText(R.string.pref_order_by_tracks_title)).check(matches(isChecked()));
        onView(withText(R.string.pref_order_by_albums_title)).perform(click());
        // Check that preferred order is changed
        String currentOrder = Utility.getPreferredOrder(InstrumentationRegistry.getTargetContext());
        assertThat(currentOrder,
                is(InstrumentationRegistry.getTargetContext().getString(R.string.pref_order_by_albums)));
    }

    @Test
    public void shouldUpdateEmptyView() {
        onView(withId(R.id.menu_artists_search)).perform(click());
        onView(withId(android.support.design.R.id.search_src_text)).perform(typeText("zoo"));
        onView(withId(R.id.empty)).check(matches(withText(R.string.not_found)));
    }

    @Test
    public void shouldSelectGenres() {
        onView(withContentDescription("More options")).perform(click());
        onView(withText(R.string.artists_menu_title_genres)).perform(click());
        onView(withText(R.string.artists_genres_selection_title)).check(matches(isDisplayed()));
        onView(withText("foo")).perform(click());
        onView(withText("bar")).perform(click());
        // Apply genres filter
        onView(withText(R.string.filter)).perform(click());
        // Snack Bar should be shown.
        onView(allOf(withId(android.support.design.R.id.snackbar_text), withText("Filtered by foo, bar")))
                .check(matches(isDisplayed()));
        // Genres are stored in preferences.
        String[] filterGenres = Utility.getFilterGenres(InstrumentationRegistry.getTargetContext());
        assertThat(filterGenres, is(new String[]{"foo", "bar"}));

        // Check that filtered is shown
        onView(withId(R.id.artists_filter)).check(matches(isDisplayed()));

        // Reset filter
        onView(withId(R.id.artists_filter)).perform(click());

        // Check that filter is reset
        // Genres are stored in preferences.
        filterGenres = Utility.getFilterGenres(InstrumentationRegistry.getTargetContext());
        assertThat(filterGenres, is(new String[0]));

    }

    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            Espresso.unregisterIdlingResources(mIdlingResource);
            mIdlingResource = null;
        }
        // Set empty genres filter
        Utility.setFilterGenres(InstrumentationRegistry.getTargetContext(),
                new String[0]);
    }
}