package com.geaden.android.mobilization.app.data;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.core.deps.guava.collect.Lists;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.geaden.android.mobilization.app.R;
import com.geaden.android.mobilization.app.models.ArtistModel;
import com.geaden.android.mobilization.app.models.ArtistModelTest;
import com.geaden.android.mobilization.app.models.ArtistModel_Table;
import com.geaden.android.mobilization.app.models.GenreModel;
import com.geaden.android.mobilization.app.models.GenreModel_ArtistModel;
import com.geaden.android.mobilization.app.util.TestDbUtil;
import com.geaden.android.mobilization.app.util.Utility;
import com.raizlabs.android.dbflow.runtime.transaction.process.InsertModelTransaction;
import com.raizlabs.android.dbflow.runtime.transaction.process.ProcessModelInfo;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Tests for implementation of artists repository.
 *
 * @author Gennady Denisov
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class ArtistsRepositoryImplTest {

    private static final String[] TEST_GENRES_ONE = {"foo", "bar", "baz"};
    private static final String[] TEST_GENRES_TWO = {"bar", "buz"};

    private static final String FOO = "foo";
    private static final String BAR = "bar";
    private static final String BUZ = "buz";

    private static final long WAIT_TIMEOUT = 1800L;
    private static final long TIMEOUT = 6000L;

    private ArtistsRepository mArtistsRepository;

    @Before
    public void setUp() throws Exception {
        TestDbUtil.cleanupDb();
        // Insert several artists with genres.
        mArtistsRepository = new ArtistsRepositoryImpl(InstrumentationRegistry.getTargetContext());
        // Reset filtered genres.
        Utility.setFilterGenres(InstrumentationRegistry.getTargetContext(), new String[0]);
        // Set default order to tracks.
        Utility.setPreferredOrder(InstrumentationRegistry.getTargetContext(),
                InstrumentationRegistry.getTargetContext().getString(R.string.pref_order_by_tracks));
        createTestData();
    }

    public void createTestData() {
        Artist artistFoo = ArtistModelTest.createArtist(FOO, BAR, TEST_GENRES_ONE);
        List<GenreModel_ArtistModel> genreArtistModels =
                artistFoo.toModel().getGenreModelArtistModelList(artistFoo.getGenres());
        Artist artistBar = ArtistModelTest.createArtist(BAR, BUZ, TEST_GENRES_TWO);
        genreArtistModels.addAll(artistBar.toModel().getGenreModelArtistModelList(
                artistBar.getGenres()));
        new InsertModelTransaction<>(ProcessModelInfo.withModels(genreArtistModels)).onExecute();
    }

    @After
    public void tearDown() throws Exception {
        TestDbUtil.cleanupDb();
    }

    @Test(timeout = TIMEOUT)
    public void testGetArtists() throws Exception {
        int expectedArtistsSize = 2;
        final List<Artist> resultArtists = Lists.newArrayList();
        mArtistsRepository.getArtists(new ArtistsRepository.LoadArtistsCallback() {
            @Override
            public void onArtistsLoaded(List<Artist> artists) {
                resultArtists.addAll(artists);
            }
        });

        waitForAsync();

        assertThat(resultArtists.size(), is(expectedArtistsSize));
    }

    @Test
    public void testRefreshData() throws Exception {
        mArtistsRepository.refreshData();
        // Artists refreshed.
        assertThat(SQLite.select().from(ArtistModel.class).queryList().size(), is(0));
        // Genres refreshed.
        assertThat(SQLite.select().from(GenreModel.class).queryList().size(), is(0));
        // GenreArtist refreshed.
        assertThat(SQLite.select().from(GenreModel_ArtistModel.class).queryList().size(), is(0));
    }

    @Test(timeout = TIMEOUT)
    public void testGetArtist() throws Exception {
        ArtistModel model = SQLite
                .select()
                .from(ArtistModel.class)
                .where(ArtistModel_Table.name.eq(FOO))
                .querySingle();
        final ArtistResultHolder resultArtist = new ArtistResultHolder();
        mArtistsRepository.getArtist(model.toArtist().getId(), new ArtistsRepository.GetArtistCallback() {
            @Override
            public void onArtistLoaded(Artist artist) {
                resultArtist.artist = artist;
            }
        });

        waitForAsync();

        assertThat(resultArtist.artist.getName(), is(FOO));
    }

    /**
     * Helper class to hold artist returned from query.
     */
    private static class ArtistResultHolder {
        Artist artist;
    }

    @Test(timeout = TIMEOUT)
    public void testFindArtistsByName() throws Exception {
        final List<Artist> resultArtists = Lists.newArrayList();
        int expectedArtistsSize = 1;
        mArtistsRepository.findArtistsByName("fo", new ArtistsRepository.LoadArtistsCallback() {
            @Override
            public void onArtistsLoaded(List<Artist> artists) {
                resultArtists.addAll(artists);

            }
        });

        waitForAsync();

        assertThat(resultArtists.size(), is(expectedArtistsSize));

    }

    /**
     * Helper method that waits some time to let async task finish.
     *
     * @throws Exception
     */
    private static void waitForAsync() throws Exception {
        Thread.sleep(WAIT_TIMEOUT);
    }

    @Test(timeout = TIMEOUT)
    public void testFindArtistsByGenres() throws Exception {
        final List<Artist> resultArtists = Lists.newArrayList();
        int expectedArtistsSize = 2;
        mArtistsRepository.findArtistsByGenres(new String[]{"bar"},
                new ArtistsRepository.LoadArtistsCallback() {
                    @Override
                    public void onArtistsLoaded(List<Artist> artists) {
                        resultArtists.addAll(artists);
                    }
                });

        waitForAsync();

        assertThat(resultArtists.size(), is(expectedArtistsSize));

        // Unique genre
        expectedArtistsSize = 1;
        resultArtists.clear();
        mArtistsRepository.findArtistsByGenres(new String[]{"foo"},
                new ArtistsRepository.LoadArtistsCallback() {
                    @Override
                    public void onArtistsLoaded(List<Artist> artists) {
                        resultArtists.addAll(artists);
                    }
                });

        waitForAsync();

        assertThat(resultArtists.size(), is(expectedArtistsSize));
    }

    @Test(timeout = TIMEOUT)
    public void testGetGenres() throws Exception {
        int expectedGenresLength = 4;
        final List<String> resultGenres = Lists.newArrayList();
        mArtistsRepository.getGenres(new ArtistsRepository.LoadGenresCallback() {
            @Override
            public void onGenresLoaded(String[] genres) {
                resultGenres.addAll(Arrays.asList(genres));
            }
        });

        waitForAsync();

        assertThat(resultGenres.size(), is(expectedGenresLength));
    }
}