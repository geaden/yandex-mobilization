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

import static org.mockito.Mockito.verify;

/**
 * Unit tests for the implementation of {@link ArtistsPresenter}.
 *
 * @author Gennady Denisov
 */
public class ArtistsPresenterTest {

    @Mock
    private ArtistsRepository mArtistsRepository;

    private static List<Artist> ARTISTS = Lists.newArrayList(new Artist(1L), new Artist(2L));

    /**
     * {@link ArgumentCaptor} to capture data passed to callback.
     */
    @Captor
    private ArgumentCaptor<ArtistsRepository.LoadArtistCallback> mLoadArtistsCallbackCaptor;

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
        mArtistsPresenter.loadArtists();

        // Check that we are showing progress indicator
        verify(mArtistsView).setProgressIndicator(true);

        // Add stub data to callback.
        verify(mArtistsRepository).getArtists(mLoadArtistsCallbackCaptor.capture());
        mLoadArtistsCallbackCaptor.getValue().onArtistsLoaded(ARTISTS);

        // Then progress indicator is hidden and artists are shown in UI
        verify(mArtistsView).setProgressIndicator(false);
        verify(mArtistsView).showArtists(ARTISTS);
    }

    @Test
    public void clickOnArtist_ShowsDetailUi() {
        // Stubbed artist
        Artist artist = new Artist(1L);

        // Request opening artist's detail
        mArtistsPresenter.openArtistDetails(artist);

        // The artists detail UI is shown
        verify(mArtistsView).showArtistDetailUi(1L);
    }
}