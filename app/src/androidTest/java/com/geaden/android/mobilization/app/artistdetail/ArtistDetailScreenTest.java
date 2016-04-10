package com.geaden.android.mobilization.app.artistdetail;

import android.app.Application;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.test.espresso.intent.Intents;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.geaden.android.mobilization.app.ArtistsApplication;
import com.geaden.android.mobilization.app.R;
import com.geaden.android.mobilization.app.di.component.DaggerTestRepositoryComponent;
import com.geaden.android.mobilization.app.di.component.TestRepositoryComponent;
import com.geaden.android.mobilization.app.di.modules.TestRepositoryModule;
import com.geaden.android.mobilization.app.util.DaggerActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.fail;

/**
 * Test for artist's detail screen.
 *
 * @author Gennady Denisov
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class ArtistDetailScreenTest {

    private CountingIdlingResource mIdlingResource;
    private TestRepositoryComponent mTestRepositoryComponent;
    /**
     * {@link ActivityTestRule} to be executed for each test method.
     * Run in lazy mode.
     */
    @Rule
    public ActivityTestRule<ArtistDetailActivity> mArtistDetailActivity =
            new DaggerActivityTestRule<>(ArtistDetailActivity.class, true, false,
                    new DaggerActivityTestRule.OnBeforeActivityLaunchedListener<ArtistDetailActivity>() {

                        @Override
                        public void beforeActivityLaunched(@NonNull Application application,
                                                           @NonNull ArtistDetailActivity activity) {
                            mIdlingResource = new CountingIdlingResource("MockArtistsRepository");
                            mTestRepositoryComponent = DaggerTestRepositoryComponent.builder()
                                    .testRepositoryModule(new TestRepositoryModule(mIdlingResource))
                                    .build();
                            ((ArtistsApplication) application).setRepositoryComponent(
                                    mTestRepositoryComponent);
                        }
                    });

    @Before
    public void intentWithStubbedArtist() {
        // Lazily start the Activity from the ActivityTestRule this time to inject the start Intent
        Intent startIntent = new Intent();
        startIntent.putExtra(ArtistDetailActivity.EXTRA_ARTIST_ID, 1L);
        mArtistDetailActivity.launchActivity(startIntent);

        registerIdlingResource();

        Intents.init();
    }

    @Test
    public void artistDetails_DisplayedInUi() throws Exception {
        // Check that artists name, description, tracks, albums and image is displayed
        onView(withId(R.id.artist_details_name)).check(matches(withText("foo")));
        onView(withId(R.id.artist_details_description)).check(matches(withText("bar")));
        onView(withId(R.id.artist_details_genres)).check(matches(withText("pop, jazz, funk")));
        onView(withId(R.id.artist_details_albums_tracks)).check(
                matches(withText("42 albums • 42 tracks")));
    }

    @Test
    public void clickOnFab_OpensBrowser() throws Exception {
        // Check that artists name, description, tracks, albums and image is displayed
        onView(withId(R.id.artist_link_fab)).perform(click());

        intended(allOf(hasAction(equalTo(Intent.ACTION_VIEW)), toPackage("com.android.browser")));
    }

    @Test
    public void incorrectArtist_showMissingText() {
        fail();
    }

    /**
     * Convenience method to register an IdlingResources with Espresso. IdlingResource resource is
     * a great way to tell Espresso when your app is in an idle state. This helps Espresso to
     * synchronize your test actions, which makes tests significantly more reliable.
     */
    private void registerIdlingResource() {
        Espresso.registerIdlingResources(mIdlingResource);
    }

    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            Espresso.unregisterIdlingResources(mIdlingResource);
            mIdlingResource = null;
        }
        Intents.release();
    }
}
