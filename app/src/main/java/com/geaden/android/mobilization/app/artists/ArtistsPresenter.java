package com.geaden.android.mobilization.app.artists;

import android.support.annotation.NonNull;

import com.geaden.android.mobilization.app.data.Artist;
import com.geaden.android.mobilization.app.data.ArtistsRepository;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Listens user actions from UI, loads data from API or internal cache and updates the UI accordingly.
 *
 * @author Gennady Denisov
 */
public class ArtistsPresenter implements ArtistsContract.UserActionsListener {

    private final ArtistsRepository mArtistsRepository;
    private final ArtistsContract.View mArtistsView;

    public ArtistsPresenter(
            @NonNull ArtistsRepository mArtistsRepository, @NonNull ArtistsContract.View mArtistsView) {
        this.mArtistsRepository = checkNotNull(mArtistsRepository, "artists repository cannot be null");
        this.mArtistsView = checkNotNull(mArtistsView, "artists view cannot be null");
    }

    @Override
    public void loadArtists(boolean forceUpdate) {
        if (forceUpdate) {
            mArtistsView.setProgressIndicator(true);
            mArtistsRepository.refreshData();
        }

        mArtistsRepository.getArtists(new ArtistsRepository.LoadArtistsCallback() {
            @Override
            public void onArtistsLoaded(List<Artist> artists) {
                mArtistsView.setProgressIndicator(false);
                mArtistsView.showArtists(artists);
            }
        });
    }

    @Override
    public void loadArtistsByName(@NonNull String artistName) {
        mArtistsView.setProgressIndicator(true);

        mArtistsRepository.findArtistsByName(artistName, new ArtistsRepository.LoadArtistsCallback() {
            @Override
            public void onArtistsLoaded(List<Artist> artists) {
                mArtistsView.setProgressIndicator(false);
                mArtistsView.showArtists(artists);
            }
        });
    }

    @Override
    public void loadArtistsByGenres(@NonNull String[] genres) {
        mArtistsView.setProgressIndicator(true);

        ArtistsRepository.LoadArtistsCallback callback = new ArtistsRepository.LoadArtistsCallback() {
            @Override
            public void onArtistsLoaded(List<Artist> artists) {
                mArtistsView.setProgressIndicator(false);
                mArtistsView.showArtists(artists);
            }
        };

        // None of the genres are selected
        // just query all artists.
        if (genres.length == 0) {
            mArtistsRepository.getArtists(callback);
            mArtistsView.hideFilteredIcon();
            return;
        }

        // Find artists by selected genres.
        mArtistsRepository.findArtistsByGenres(genres, callback);

        // Show icon, that the result if filtered.
        mArtistsView.showFilteredIcon();
    }

    @Override
    public void openArtistDetails(@NonNull Artist requestedArtist, @NonNull android.view.View coverView) {
        checkNotNull(requestedArtist, "requested artist cannot be null");
        mArtistsView.showArtistDetailUi(requestedArtist.getId(), coverView);
    }

    @Override
    public void loadGenres() {
        mArtistsRepository.getGenres(new ArtistsRepository.LoadGenresCallback() {
            @Override
            public void onGenresLoaded(String[] genres) {
                mArtistsView.showSelectGenresDialog(genres);
            }
        });
    }

    @Override
    public void resetFilter() {
        mArtistsView.setFilteredGenres(new String[0]);
        mArtistsView.hideFilteredIcon();
        loadArtists(false);
    }

    @Override
    public void selectOrder() {
        mArtistsView.showSelectOrderDialog();
    }
}
