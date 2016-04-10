package com.geaden.android.mobilization.app.di.component;

import com.geaden.android.mobilization.app.artistdetail.ArtistDetailFragment;
import com.geaden.android.mobilization.app.artists.ArtistsFragment;
import com.geaden.android.mobilization.app.di.module.RepositoryModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Dagger component for injections of repository.
 *
 * @author Gennady Denisov
 */
@Singleton
@Component(modules = RepositoryModule.class)
public interface RepositoryComponent {
    void inject(ArtistsFragment fragment);

    void inject(ArtistDetailFragment fragment);
}
