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

    /**
     * Gets artist by id from repository.
     *
     * @param artistId artist id to load data for.
     * @param callback callback, that handles when artist loaded.
     */
    void getArtist(@NonNull long artistId, @NonNull GetArtistCallback callback);

    /**
     * Refreshes data.
     */
    void refreshData();

    /**
     * Callback to handle get note method.
     */
    interface GetArtistCallback {
        /**
         * Called when artist loaded from repository (cache or network).
         *
         * @param artist loaded artist.
         */
        void onArtistLoaded(Artist artist);
    }
}
