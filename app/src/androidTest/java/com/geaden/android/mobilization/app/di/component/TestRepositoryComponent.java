package com.geaden.android.mobilization.app.di.component;

import com.geaden.android.mobilization.app.di.modules.TestRepositoryModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Repository component for testing.
 *
 * @author Gennady Denisov
 */
@Singleton
@Component(modules = {TestRepositoryModule.class})
public interface TestRepositoryComponent extends RepositoryComponent {

}
