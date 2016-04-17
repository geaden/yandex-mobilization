package com.geaden.android.mobilization.app.data;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation to mark connection status or loading artists state.
 *
 * @author Gennady Denisov
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({
        LoadingStatus.LOADING,
        LoadingStatus.NOT_FOUND,
        LoadingStatus.NETWORK_ERROR,
        LoadingStatus.ERROR
})
public @interface LoadingStatus {
    int LOADING = 0;
    int NOT_FOUND = 1;
    int NETWORK_ERROR = 2;
    int ERROR = 3;
}
