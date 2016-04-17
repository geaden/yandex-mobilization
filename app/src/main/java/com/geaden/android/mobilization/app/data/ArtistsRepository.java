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
    interface LoadArtistsCallback {
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
    void getArtists(@NonNull LoadArtistsCallback callback);

    /**
     * Gets artist by id from repository.
     *
     * @param artistId artist id to load data for.
     * @param callback callback handling artist loaded.
     */
    void getArtist(@NonNull long artistId, @NonNull GetArtistCallback callback);

    /**
     * Finds artists by name.
     *
     * @param query    the query to retrieve artists.
     * @param callback callback handling artists found.
     */
    void findArtistsByName(@NonNull String query, @NonNull LoadArtistsCallback callback);

    /**
     * Finds artists by genres.
     *
     * @param genres   genres that artists have.
     * @param callback callback handling artists loading.
     */
    void findArtistsByGenres(@NonNull String[] genres, @NonNull LoadArtistsCallback callback);

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

    /**
     * Method to load genres.
     *
     * @param callback the callback handling genres loading.
     */
    void getGenres(@NonNull LoadGenresCallback callback);


    /**
     * Callback to handle genres loading.
     */
    interface LoadGenresCallback {
        /**
         * Called when genres are loaded.
         *
         * @param genres array of genres.
         */
        void onGenresLoaded(String[] genres);
    }
}
