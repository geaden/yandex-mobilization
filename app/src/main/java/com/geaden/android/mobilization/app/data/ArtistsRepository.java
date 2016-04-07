package com.geaden.android.mobilization.app.data;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Main entry point for accessing artists' data.
 *
 * @author Gennady Denisov
 */
public interface ArtistsRepository {
    /**
     * Defines actions performed when data loaded.
     */
    interface LoadArtistCallback {
        /**
         * Called when artists are loaded.
         *
         * @param artists list of returned artists.
         */
        void onArtistsLoaded(List<Artist> artists);
    }

    /**
     * Gets artists.
     *
     * @param callback on loaded artists callback.
     */
    void getArtists(@NonNull LoadArtistCallback callback);
}
