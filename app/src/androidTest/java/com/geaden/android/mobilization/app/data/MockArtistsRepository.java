package com.geaden.android.mobilization.app.data;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.test.espresso.core.deps.guava.collect.Lists;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.v4.util.ArrayMap;

import com.geaden.android.mobilization.app.util.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Mock repository implementation to provide mock data.
 *
 * @author Gennady Denisov
 */
public class MockArtistsRepository implements ArtistsRepository {

    private static final ArrayMap<Long, Artist> ARTISTS_DATA;
    private static final long LONG_SERVICE_LATENCY_IN_MILLIS = 2000;
    private static final long SHORT_SERVICE_LATENCY_IN_MILLIS = 1000;

    private static final String SMALL = "file:///android_asset/lenna.png";
    private static final String BIG = "file:///android_asset/lenna.png";

    private static final String[] TEST_GENRES = {"pop", "jazz", "funk"};

    private static final AtomicLong atomicId = new AtomicLong(1L);

    // Idling resource to tell Espresso to wait...
    private final CountingIdlingResource mCountingIdlingResource;
    private final Context mContext;

    public MockArtistsRepository(Context context,
                                 CountingIdlingResource countingIdlingResource) {
        mContext = context;
        mCountingIdlingResource = countingIdlingResource;
    }

    static {
        ARTISTS_DATA = new ArrayMap<>(3);
        addArtist("foo", "bar");
        addArtist("bar", "buz");
        addArtist("doo", "duz");
    }


    @Override
    public void getArtists(@NonNull final LoadArtistsCallback callback) {
        Utility.setLoadingStatus(mContext, LoadingStatus.LOADING);
        mCountingIdlingResource.increment();
        // Simulate network by delaying the execution.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCountingIdlingResource.decrement();
                List<Artist> artists = new ArrayList<>(ARTISTS_DATA.values());
                callback.onArtistsLoaded(artists);
            }
        }, LONG_SERVICE_LATENCY_IN_MILLIS);
    }

    private static void addArtist(String name, String description) {
        Artist newArtist = new Artist(atomicId.getAndDecrement(), name, description);
        newArtist.setGenres(TEST_GENRES);
        newArtist.setTracks(42);
        newArtist.setAlbums(42);
        newArtist.setLink("https://ya.ru");
        newArtist.setCover(new Cover(SMALL, BIG));
        ARTISTS_DATA.put(newArtist.getId(), newArtist);
    }

    @Override
    public void getArtist(@NonNull long artistId, @NonNull GetArtistCallback callback) {
        Artist artist = ARTISTS_DATA.get(artistId);
        callback.onArtistLoaded(artist);
    }

    @Override
    public void refreshData() {

    }

    @Override
    public void findArtistsByName(@NonNull final String query, @NonNull final LoadArtistsCallback callback) {
        Utility.setLoadingStatus(mContext, LoadingStatus.LOADING);
        mCountingIdlingResource.increment();
        // Simulate network by delaying the execution.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCountingIdlingResource.decrement();
                List<Artist> foundArtists = Lists.newArrayList();
                for (Artist artist : ARTISTS_DATA.values()) {
                    if (artist.getName().toLowerCase().contains(query.toLowerCase())) {
                        foundArtists.add(artist);
                    }
                }
                if (foundArtists.size() == 0) {
                    Utility.setLoadingStatus(mContext, LoadingStatus.NOT_FOUND);
                }
                callback.onArtistsLoaded(foundArtists);
            }
        }, SHORT_SERVICE_LATENCY_IN_MILLIS);
    }

    @Override
    public void findArtistsByGenres(@NonNull String[] genres, @NonNull LoadArtistsCallback callback) {
        List<Artist> artists = new ArrayList<>(ARTISTS_DATA.values());
        callback.onArtistsLoaded(artists);
    }

    @Override
    public void getGenres(@NonNull final LoadGenresCallback callback) {
        // Delay the execution.
        mCountingIdlingResource.increment();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCountingIdlingResource.decrement();
                callback.onGenresLoaded(new String[]{"foo", "bar"});
            }
        }, SHORT_SERVICE_LATENCY_IN_MILLIS);
    }
}
