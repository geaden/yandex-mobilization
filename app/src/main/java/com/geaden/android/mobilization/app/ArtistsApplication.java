package com.geaden.android.mobilization.app;

import android.app.Application;
import android.support.annotation.VisibleForTesting;

import com.geaden.android.mobilization.app.di.component.DaggerRepositoryComponent;
import com.geaden.android.mobilization.app.di.component.RepositoryComponent;
import com.geaden.android.mobilization.app.di.module.RepositoryModule;
import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * Main artists application.
 *
 * @author Gennady Denisov
 */
public class ArtistsApplication extends Application {
    private RepositoryComponent mRepositoryComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        FlowManager.init(this);
        mRepositoryComponent = DaggerRepositoryComponent.builder()
                .repositoryModule(new RepositoryModule(this))
                .build();
    }

    public RepositoryComponent getRepositoryComponent() {
        return mRepositoryComponent;
    }

    @VisibleForTesting
    public void setRepositoryComponent(RepositoryComponent repositoryComponent) {
        mRepositoryComponent = repositoryComponent;
    }
}


