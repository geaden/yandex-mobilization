package com.geaden.android.mobilization.app.artists;

import com.geaden.android.mobilization.app.data.Artist;
import com.geaden.android.mobilization.app.data.ArtistsRepository;
import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the implementation of {@link ArtistsPresenter}.
 *
 * @author Gennady Denisov
 */
public class ArtistsPresenterTest {

    @Mock
    private ArtistsRepository mArtistsRepository;

    @Mock
    private android.view.View mCoverView;

    private static final List<Artist> ARTISTS = Lists.newArrayList(new Artist(1L, "foo", "bar"),
            new Artist(2L, "fuz", "buz"));

    private static final List<Artist> FOUND_ARTISTS = Lists.newArrayList(new Artist(3L, "buz", "boo"));

    private static final String[] GENRES = {"foo", "bar"};

    /**
     * {@link ArgumentCaptor} to capture data passed to callback.
     */
    @Captor
    private ArgumentCaptor<ArtistsRepository.LoadArtistsCallback> mLoadArtistsCallbackCaptor;

    @Captor
    private ArgumentCaptor<ArtistsRepository.LoadGenresCallback> mLoadGenresCallbackCaptor;

    @Mock
    private ArtistsContract.View mArtistsView;
    private ArtistsPresenter mArtistsPresenter;

    @Before
    public void setupArtistsPresenter() {
        // Inject the mocks.
        MockitoAnnotations.initMocks(this);

        // Initialize artists presenter to test
        mArtistsPresenter = new ArtistsPresenter(mArtistsRepository, mArtistsView);
    }

    @Test
    public void loadArtistsFromRepositoryAndLoadIntoView() {
        // Request artists.
        mArtistsPresenter.loadArtists(true);

        verify(mArtistsRepository).refreshData();

        // Check that we are showing progress indicator.
        verify(mArtistsView).setProgressIndicator(true);

        // Add stub data to callback.
        verify(mArtistsRepository).getArtists(mLoadArtistsCallbackCaptor.capture());
        mLoadArtistsCallbackCaptor.getValue().onArtistsLoaded(ARTISTS);

        // Then progress indicator is hidden and artists are shown in UI
        verify(mArtistsView).setProgressIndicator(false);
        verify(mArtistsView).showArtists(ARTISTS);
    }

    @Test
    public void findArtistsByNameAndLoadIntoView() {
        // Find artists by name.
        mArtistsPresenter.loadArtistsByName("buz");

        // Check that we are showing progress indicator.
        verify(mArtistsView).setProgressIndicator(true);

        // Add stub to callback on find artists.
        verify(mArtistsRepository).findArtistsByName(eq("buz"), mLoadArtistsCallbackCaptor.capture());
        mLoadArtistsCallbackCaptor.getValue().onArtistsLoaded(FOUND_ARTISTS);

        // The progress indicator is hidden and found artists artists are shown in UI.
        verify(mArtistsView).setProgressIndicator(false);
        verify(mArtistsView).showArtists(FOUND_ARTISTS);
    }

    @Test
    public void findArtistsByGenresLoadIntoView() {
        mArtistsPresenter.loadArtistsByGenres(GENRES, false);

        // Check that we are showing progress indicator.
        verify(mArtistsView).setProgressIndicator(true);

        // Add stub to callback on find artists.
        verify(mArtistsRepository).findArtistsByGenres(eq(GENRES), mLoadArtistsCallbackCaptor.capture());
        mLoadArtistsCallbackCaptor.getValue().onArtistsLoaded(FOUND_ARTISTS);

        // The progress indicator is hidden and found artists artists are shown in UI.
        verify(mArtistsView).setProgressIndicator(false);
        verify(mArtistsView).showArtists(FOUND_ARTISTS);
    }

    @Test
    public void selectOrderShowsDialog() {
        mArtistsPresenter.selectOrder();
        verify(mArtistsView).showSelectOrderDialog();
    }

    @Test
    public void loadGenresShowsDialog() {
        mArtistsPresenter.loadGenres();

        verify(mArtistsRepository).getGenres(mLoadGenresCallbackCaptor.capture());
        mLoadGenresCallbackCaptor.getValue().onGenresLoaded(GENRES);

        // Dialog with required genres is shown
        mArtistsView.showSelectGenresDialog(GENRES);
    }

    @Test
    public void clickOnArtist_ShowsDetailUi() {
        // Stubbed artist
        Artist artist = new Artist(1L, "foo", "bar");

        // Request opening artist's detail
        mArtistsPresenter.openArtistDetails(artist, mCoverView);

        // The artists detail UI is shown
        verify(mArtistsView).showArtistDetailUi(1L, mCoverView);
    }
}