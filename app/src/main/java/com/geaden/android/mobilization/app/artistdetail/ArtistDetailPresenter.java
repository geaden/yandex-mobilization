package com.geaden.android.mobilization.app.artistdetail;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.geaden.android.mobilization.app.data.Artist;
import com.geaden.android.mobilization.app.data.ArtistsRepository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Listens to user actions from UI and updates the view accordingly.
 *
 * @author Gennady Denisov
 */
public class ArtistDetailPresenter implements ArtistDetailContract.UserActionsListener {
    private final ArtistsRepository mArtistsRepository;
    private final ArtistDetailContract.View mArtistDetailView;

    public ArtistDetailPresenter(@NonNull ArtistsRepository artistsRepository,
                                 @NonNull ArtistDetailContract.View artistDetailView) {
        mArtistsRepository = checkNotNull(artistsRepository, "artists repository cannot be null!");
        mArtistDetailView = checkNotNull(artistDetailView, "artists detail view cannot be null!");
    }

    @Override
    public void openArtist(long artistId) {
        mArtistDetailView.setProgressIndicator(true);
        mArtistsRepository.getArtist(artistId, new ArtistsRepository.GetArtistCallback() {
            @Override
            public void onArtistLoaded(Artist artist) {
                mArtistDetailView.setProgressIndicator(false);
                if (null == artist) {
                    mArtistDetailView.showMissingArtist();
                } else {
                    showArtist(artist);
                }
            }
        });
    }

    @Override
    public void openArtistLink(Activity activity, String artistLink) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(artistLink));
        activity.startActivity(intent);
    }

    /**
     * Helper method to populate details view with artist's data.
     *
     * @param artist instance of {@link Artist}
     */
    private void showArtist(Artist artist) {
        String name = artist.getName();
        String description = artist.getDescription();
        String coverImageUrl = artist.getCover().getBig();
        String artistLink = artist.getLink();
        int tracks = artist.getTracks();
        int albums = artist.getAlbums();
        String[] genres = artist.getGenres();
        mArtistDetailView.showName(name);
        mArtistDetailView.showDescription(description);
        mArtistDetailView.showGenres(genres);
        mArtistDetailView.showAlbumsAndTracks(albums, tracks);
        mArtistDetailView.showCover(coverImageUrl);
        if (null != artistLink) {
            mArtistDetailView.showOpenArtistLinkFab(artistLink);
        } else {
            mArtistDetailView.hideOpenArtistLinkFab();
        }
    }
}
