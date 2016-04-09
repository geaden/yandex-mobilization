package com.geaden.android.mobilization.app.di.component;

import com.geaden.android.mobilization.app.artists.ArtistsFragment;
import com.geaden.android.mobilization.app.di.module.RepositoryModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * {}.
 *
 * @author Gennady Denisov
 */
@Singleton
@Component(modules = RepositoryModule.class)
public interface RepositoryComponent {
    void inject(ArtistsFragment fragment);
}
