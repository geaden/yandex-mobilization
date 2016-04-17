package com.geaden.android.mobilization.app.data;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.test.espresso.core.deps.guava.collect.Lists;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.v4.util.ArrayMap;

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
    private static final long SERVICE_LATENCY_IN_MILLIS = 2000;

    private static final String SMALL = "file:///android_asset/lenna.png";
    private static final String BIG = "file:///android_asset/lenna.png";

    private static final String[] TEST_GENRES = {"pop", "jazz", "funk"};

    private static final AtomicLong atomicId = new AtomicLong(1L);

    // Idling resource to tell Espresso to wait...
    private final CountingIdlingResource mCountingIdlingResource;

    public MockArtistsRepository(CountingIdlingResource countingIdlingResource) {
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
        }, SERVICE_LATENCY_IN_MILLIS);
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
                callback.onArtistsLoaded(foundArtists);
            }
        }, 1000);
    }

    @Override
    public void findArtistsByGenres(@NonNull String[] genres, @NonNull LoadArtistsCallback callback) {

    }
}
