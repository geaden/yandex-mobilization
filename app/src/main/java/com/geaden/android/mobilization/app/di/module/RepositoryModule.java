package com.geaden.android.mobilization.app.di.module;

import android.content.Context;

import com.geaden.android.mobilization.app.data.ArtistsRepository;
import com.geaden.android.mobilization.app.data.ArtistsRepositoryImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Module to provide repository..
 *
 * @author Gennady Denisov
 */
@Module
public class RepositoryModule {

    private Context mContext;

    public RepositoryModule(Context context) {
        mContext = context;
    }

    @Provides
    @Singleton
    ArtistsRepository provideArtistRepository() {
        return new ArtistsRepositoryImpl(mContext);
    }
}
