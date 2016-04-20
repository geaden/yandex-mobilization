package com.geaden.android.mobilization.app.artists;

import android.app.Application;
import android.preference.Preference;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.test.espresso.matcher.PreferenceMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.widget.DrawerLayout;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.Gravity;

import com.geaden.android.mobilization.app.ArtistsApplication;
import com.geaden.android.mobilization.app.BuildConfig;
import com.geaden.android.mobilization.app.R;
import com.geaden.android.mobilization.app.di.component.DaggerTestRepositoryComponent;
import com.geaden.android.mobilization.app.di.component.TestRepositoryComponent;
import com.geaden.android.mobilization.app.di.modules.TestRepositoryModule;
import com.geaden.android.mobilization.app.util.ArtistsViewInteractions;
import com.geaden.android.mobilization.app.util.DaggerActivityTestRule;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;
import static android.support.test.espresso.contrib.NavigationViewActions.navigateTo;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Tests for the {@link DrawerLayout} layout component in {@link ArtistsActivity} which manages
 * navigation within the app.
 *
 * @author Gennady Denisov
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class AppNavigationTest {

    private TestRepositoryComponent mTestRepositoryComponent;
    private CountingIdlingResource mIdlingResource;
    /**
     * {@link ActivityTestRule} is a JUnit {@link Rule @Rule} to launch your activity under test.
     * <p/>
     * <p/>
     * Rules are interceptors which are executed for each test method and are important building
     * blocks of Junit tests.
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
        Espresso.registerIdlingResources(mIdlingResource);
    }

    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            Espresso.unregisterIdlingResources(mIdlingResource);
            mIdlingResource = null;
        }
    }

    @Test
    public void clickOnAboutNavigationItem_ShowsAboutScreen() throws Exception {
        // Open Drawer to click on navigation.
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(open()); // Open Drawer

        // Start about screen.
        onView(withId(R.id.nav_view))
                .perform(navigateTo(R.id.about_menu_item));

        // Check that about Activity was opened.
        String aboutTitle = InstrumentationRegistry.getTargetContext().getString(R.string.about_title);
        ArtistsViewInteractions.matchToolbarTitle(aboutTitle);

        onData(PreferenceMatchers.withSummaryText(BuildConfig.VERSION_NAME)).check(matches(isDisplayed()));

        Matcher<Preference> mobilizationPref = PreferenceMatchers.withKey(
                InstrumentationRegistry.getTargetContext().getString(R.string.pref_key_mobilization));

        onData(mobilizationPref).check(matches(isDisplayed()));
    }

    @Test
    public void clickOnAndroidHomeIcon_OpensNavigation() {
        // Check initial title
        String title = InstrumentationRegistry.getTargetContext().getString(R.string.artists_title);
        ArtistsViewInteractions.matchToolbarTitle(title);

        // Check that left drawer is closed at startup
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))); // Left Drawer should be closed.

        // Open Drawer
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(open());

        // Check if drawer is open
        onView(withId(R.id.drawer_layout))
                .check(matches(isOpen(Gravity.LEFT))); // Left drawer is open.

        // Check title has changed.
        title = InstrumentationRegistry.getTargetContext().getString(R.string.app_name);
        ArtistsViewInteractions.matchToolbarTitle(title);
    }

}
