package com.geaden.android.mobilization.app.artistdetail;

import android.app.Activity;
import android.support.v4.app.ShareCompat;

import java.util.List;

/**
 * Specifies contract between the view and the presenter.
 *
 * @author Gennady Denisov
 */
public interface ArtistDetailContract {
    /**
     * Defines Artist Detail View
     */
    interface View {
        /**
         * Shows/hides progress indicator.
         *
         * @param active whether to show or hide progress indicator.
         */
        void setProgressIndicator(boolean active);

        /**
         * Shows artist's name.
         *
         * @param name the name of the artist to show.
         */
        void showName(String name);

        /**
         * Shows artist's description.
         *
         * @param description artist's description to show.
         */
        void showDescription(String description);

        /**
         * Shows cover.
         *
         * @param coverLink link to a cover.
         */
        void showCover(String coverLink);

        /**
         * Shows help message, if artist is missing.
         */
        void showMissingArtist();

        /**
         * Shows available genres.
         *
         * @param genres available genres.
         */
        void showGenres(List<String> genres);

        /**
         * Shows number of tracks published by the artist.
         *
         * @param tracks number of tracks published by the artist.
         */
        void showTracks(int tracks);

        /**
         * Show number of albums published by the artist.
         *
         * @param albums number of albums published by the artist.
         */
        void showAlbums(int albums);
    }

    interface UserActionsListener {

        /**
         * Opens requested artist details.
         *
         * @param artistId requested artist id.
         */
        void openArtist(long artistId);

        /**
         * Opens the link to the artist's page.
         *
         * @param activity   the Activity to get {@link ShareCompat} from.
         * @param artistLink link to the artist's page.
         */
        void openArtistLink(Activity activity, String artistLink);

    }
}
