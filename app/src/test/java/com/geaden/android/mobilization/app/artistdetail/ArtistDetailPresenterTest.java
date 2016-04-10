package com.geaden.android.mobilization.app.artistdetail;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.geaden.android.mobilization.app.data.Artist;
import com.geaden.android.mobilization.app.data.ArtistsRepository;
import com.geaden.android.mobilization.app.data.Cover;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test for implementation of {@link ArtistDetailPresenter}.
 *
 * @author Gennady Denisov
 */
@PrepareForTest({Uri.class, Intent.class})
@RunWith(PowerMockRunner.class)
public class ArtistDetailPresenterTest {

    private final static String TEST_NAME = "foo";
    private final static String TEST_DESCRIPTION = "bar";
    private static final String SMALL_COVER = "small";
    private static final String BIG_COVER = "big";
    private static final String TEST_LINK = "test_link";
    private static final String[] TEST_GENRES = {"foo", "bar"};
    private static final int TEST_ALBUMS = 10;
    private static final int TEST_TRACKS = 10;
    private static final long INVALID_ID = -1L;

    @Mock
    private ArtistsRepository mArtistsRepository;

    @Mock
    private ArtistDetailContract.View mArtistDetailView;

    @Mock
    private Activity activity;

    @Mock
    private Uri uri;

    @Mock
    private Intent intent;

    private ArtistDetailPresenter mAritstDetailPresenter;

    @Captor
    private ArgumentCaptor<ArtistsRepository.GetArtistCallback> mGetArtistArgumentCaptor;

    @Before
    public void setupArtistsPresenter() throws Exception {
        MockitoAnnotations.initMocks(this);
        // Mock static class
        PowerMockito.mockStatic(Uri.class);
        when(Uri.parse(TEST_LINK)).thenReturn(uri);
        PowerMockito.whenNew(Intent.class).withArguments(Intent.ACTION_VIEW).thenReturn(intent);
        mAritstDetailPresenter = new ArtistDetailPresenter(mArtistsRepository, mArtistDetailView);
    }

    /**
     * Gets stubbed artist.
     *
     * @return stubbed artist.
     */
    private Artist stubArtist() {
        Artist artist = new Artist(1L, TEST_NAME, TEST_DESCRIPTION);
        artist.setCover(new Cover(SMALL_COVER, BIG_COVER));
        artist.setAlbums(TEST_ALBUMS);
        artist.setTracks(TEST_TRACKS);
        artist.setLink(TEST_LINK);
        artist.setGenres(TEST_GENRES);
        return artist;
    }

    @Test
    public void getArtistFromRepositoryAndLoadIntoView() {
        // Stub returned artist.
        Artist artist = stubArtist();

        // When artist detail presenter is asked to open an artist's detail
        mAritstDetailPresenter.openArtist(artist.getId());

        // Then artist is loaded from repository, callback is captured and progress indicator is shown.
        verify(mArtistsRepository).getArtist(eq(artist.getId()), mGetArtistArgumentCaptor.capture());
        verify(mArtistDetailView).setProgressIndicator(true);

        // When artist is finally loaded.
        mGetArtistArgumentCaptor.getValue().onArtistLoaded(artist);

        // Then progress is hidden and artist's information is displayed in UI.
        verify(mArtistDetailView).setProgressIndicator(false);
        verify(mArtistDetailView).showName(TEST_NAME);
        verify(mArtistDetailView).showDescription(TEST_DESCRIPTION);
        verify(mArtistDetailView).showAlbumsAndTracks(TEST_ALBUMS, TEST_TRACKS);
        verify(mArtistDetailView).showCover(BIG_COVER);
        verify(mArtistDetailView).showGenres(TEST_GENRES);
    }

    @Test
    public void getUnknownArtistFromRepositoryAndLoadIntoView() {
        // When loading of an artist is requested with an invalid artist ID.
        mAritstDetailPresenter.openArtist(INVALID_ID);

        // Then artist with invalid id is attempted to load from model, callback is captured and
        // progress indicator is shown.
        verify(mArtistDetailView).setProgressIndicator(true);
        verify(mArtistsRepository).getArtist(eq(INVALID_ID), mGetArtistArgumentCaptor.capture());

        // When artist is finally loaded
        mGetArtistArgumentCaptor.getValue().onArtistLoaded(null);

        // Then progress indicator is hidden and missing artist UI is shown
        verify(mArtistDetailView).setProgressIndicator(false);
        verify(mArtistDetailView).showMissingArtist();
    }

    @Test
    @Ignore
    public void clickOnShare_SharesArtistLink() {
        when(intent.setData(uri)).thenReturn(intent);

        // When opening artist link.
        mAritstDetailPresenter.openArtistLink(activity, TEST_LINK);

        verify(intent).setData(any(Uri.class));
        verify(activity).startActivity(intent);
    }

}