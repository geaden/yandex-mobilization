package com.geaden.android.mobilization.app.models;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.geaden.android.mobilization.app.data.Artist;
import com.geaden.android.mobilization.app.data.Cover;
import com.geaden.android.mobilization.app.util.TestDbUtil;
import com.raizlabs.android.dbflow.runtime.transaction.process.InsertModelTransaction;
import com.raizlabs.android.dbflow.runtime.transaction.process.ProcessModelInfo;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for artists model.
 *
 * @author Gennady Denisov
 */
@SmallTest
@RunWith(AndroidJUnit4.class)
public class ArtistModelTest {
    public static final String TEST_NAME = "foo";
    public static final String TEST_DESCRIPTION = "bar";

    public static final String[] TEST_GENRES = {"foo", "bar"};

    public static final String SMALL = "small";
    public static final String BIG = "big";

    public static final String TEST_LINK = "https://ya.ru";

    public static final int FORTY_TWO = 42;

    public static final AtomicLong atomicId = new AtomicLong(1L);

    @Before
    public void deleteDataFromDb() {
        TestDbUtil.cleanupDb();
    }

    @Test
    public void loadArtistsFromDb() {
        Artist artist = createArtist(TEST_NAME, TEST_DESCRIPTION, TEST_GENRES);

        List<GenreModel_ArtistModel> genreModelArtistModels = artist.toModel()
                .getGenreModelArtistModelList(artist.getGenres());

        // Save artist along with genres.
        new InsertModelTransaction<>(ProcessModelInfo.withModels(genreModelArtistModels)).onExecute();

        List<ArtistModel> models = SQLite.select().from(ArtistModel.class).queryList();
        assertTrue(models.size() == 1);

        ArtistModel model = models.get(0);

        Artist queriedArtist = model.toArtist();

        assertEquals(TEST_NAME, queriedArtist.getName());
        assertEquals(TEST_DESCRIPTION, queriedArtist.getDescription());
        assertEquals(TEST_LINK, queriedArtist.getLink());
        assertArrayEquals(TEST_GENRES, queriedArtist.getGenres());
        assertEquals(FORTY_TWO, queriedArtist.getTracks());
        assertEquals(FORTY_TWO, queriedArtist.getAlbums());
        assertEquals(SMALL, queriedArtist.getCover().getSmall());
        assertEquals(BIG, queriedArtist.getCover().getBig());
    }

    /**
     * Creates new artist without saving to database.
     *
     * @param name        artist's name.
     * @param description artist's description.
     * @param genres      artist's genres.
     * @return new artists instance.
     */
    public static Artist createArtist(String name, String description, String[] genres) {
        Artist newArtist = new Artist(atomicId.getAndDecrement(), name, description);
        newArtist.setGenres(genres);
        newArtist.setTracks(FORTY_TWO);
        newArtist.setAlbums(FORTY_TWO);
        newArtist.setLink("https://ya.ru");
        newArtist.setCover(new Cover(SMALL, BIG));
        return newArtist;
    }
}