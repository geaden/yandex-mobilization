package com.geaden.android.mobilization.app.artists;

import android.support.annotation.NonNull;

import com.geaden.android.mobilization.app.data.Artist;

import java.util.List;

/**
 * Specifies the contract between Artists view and Artists Presenter.
 *
 * @author Gennady Denisov
 */
public interface ArtistsContract {
    // The Artists View
    interface View {
        /**
         * Sets progress indicator to an active or inactive state.
         *
         * @param active if progress indicator should be shown.
         */
        void setProgressIndicator(boolean active);

        /**
         * Shows list of artists.
         *
         * @param artists list of artists to be shown.
         */
        void showArtists(List<Artist> artists);

        /**
         * Shows artist's details.
         *
         * @param artistId  artist Id to load data for.
         * @param coverView artist's cover.
         */
        void showArtistDetailUi(long artistId, android.view.View coverView);

        /**
         * Shows select order dialog.
         */
        void showSelectOrderDialog();

        /**
         * Shows select genres dialog.
         *
         * @param genres available genres for selection.
         */
        void showSelectGenresDialog(String[] genres);

        /**
         * Method to update empty view when needed.
         */
        void updateEmptyView();

        /**
         * Gets array of genres filtered by user.
         *
         * @return array of filtered genres.
         */
        String[] getFilteredGenres();

        /**
         * Sets array of genres as filtered.
         *
         * @param genres filtered genres.
         */
        void setFilteredGenres(String[] genres);

        /**
         * Shows icon, indicating result is filtered.
         */
        void showFilteredIcon();

        /**
         * Hides icon, that the result if filtered.
         */
        void hideFilteredIcon();
    }

    /**
     * Defines actions that can be started from the Artists view.
     */
    interface UserActionsListener {
        /**
         * Loads artists from internal repository or Api.
         *
         * @param forceUpdate flag that indicates to re-retrieve data from network.
         */
        void loadArtists(boolean forceUpdate);

        /**
         * Loads artists by provided name.
         *
         * @param artistName name or part of the name of the artist.
         */
        void loadArtistsByName(@NonNull String artistName);

        /**
         * Loads artists by genres.
         *
         * @param genres array of selected genres that artists have.
         */
        void loadArtistsByGenres(@NonNull String[] genres);

        /**
         * Loads list of genres to select.
         */
        void loadGenres();

        /**
         * Allows user to select order.
         */
        void selectOrder();

        /**
         * Resets genres filter.
         */
        void resetFilter();

        /**
         * Opens artists details.
         *
         * @param requestedArtist instance of {@link Artist} to open details for.
         * @param coverView       artist's cover.
         */
        void openArtistDetails(@NonNull Artist requestedArtist, @NonNull android.view.View coverView);
    }
}
