package com.geaden.android.mobilization.app.artists;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.geaden.android.mobilization.app.ArtistsApplication;
import com.geaden.android.mobilization.app.R;
import com.geaden.android.mobilization.app.di.component.DaggerTestRepositoryComponent;
import com.geaden.android.mobilization.app.di.component.RepositoryComponent;
import com.geaden.android.mobilization.app.di.modules.TestRepositoryModule;
import com.geaden.android.mobilization.app.util.DaggerActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Tests for main screen with artists grid.
 * Uses {@link CountingIdlingResource} to make sure Espresso whaits for artists to be loaded from repository.
 * Example is taken from
 * <a href="https://android.googlesource.com/platform/frameworks/testing/+/android-support-test/espresso/contrib/src/main/java/android/support/test/espresso/contrib/CountingIdlingResource.java">
 * here
 * </a>.
 *
 * @author Gennady Denisov
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ArtistsActivityScreen {
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
                                    .testRepositoryModule(new TestRepositoryModule(mIdlingResource))
                                    .build();
                            ((ArtistsApplication) application).setRepositoryComponent(mTestRepositoryComponent);
                        }
                    });

    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            Espresso.unregisterIdlingResources(mIdlingResource);
            mIdlingResource = null;
        }
    }

    @Before
    public void registerIdlingResource() {
        Espresso.registerIdlingResources(mIdlingResource);
    }


    @Test
    public void showEmptyArtistsList() {
        // Shows empty artists...
        onView(withId(R.id.empty)).check(matches(isDisplayed()));

    }

}