package com.geaden.android.mobilization.app.di.modules;

import android.support.test.espresso.idling.CountingIdlingResource;

import com.geaden.android.mobilization.app.data.ArtistsRepository;
import com.geaden.android.mobilization.app.data.MockArtistsRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Repository module for testing.
 *
 * @author Gennady Denisov
 */
@Module
public class TestRepositoryModule {

    // Idling resource to tell Espresso that some background task is running
    private final CountingIdlingResource mCountingIdlingResource;

    public TestRepositoryModule(CountingIdlingResource countingIdlingResource) {
        mCountingIdlingResource = countingIdlingResource;
    }

    @Provides
    @Singleton
    ArtistsRepository provideArtistsRepository() {
        return new MockArtistsRepository(mCountingIdlingResource);
    }
}
