package com.geaden.android.mobilization.app.models;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.geaden.android.mobilization.app.data.Artist;
import com.geaden.android.mobilization.app.data.Cover;
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
    private final String TEST_NAME = "foo";
    private final String TEST_DESCRIPTION = "bar";

    private final String[] TEST_GENRES = {"foo", "bar"};

    private final String SMALL = "small";
    private final String BIG = "big";

    private final String TEST_LINK = "https://ya.ru";

    private final int FORTY_TWO = 42;

    private final AtomicLong atomicId = new AtomicLong(1L);

    @Before
    public void deleteDataFromDb() {
        SQLite.delete(ArtistModel.class).query();
    }

    @Test
    public void loadArtistsFromDb() {
        Artist newArtist = new Artist(atomicId.getAndDecrement(),
                TEST_NAME, TEST_DESCRIPTION);
        newArtist.setGenres(TEST_GENRES);
        newArtist.setTracks(FORTY_TWO);
        newArtist.setAlbums(FORTY_TWO);
        newArtist.setLink("https://ya.ru");
        newArtist.setCover(new Cover(SMALL, BIG));

        newArtist.toModel().save();

        List<ArtistModel> models = SQLite.select().from(ArtistModel.class).queryList();
        assertTrue(models.size() == 1);

        ArtistModel model = models.get(0);

        Artist artist = model.toArtist();

        assertEquals(Long.valueOf(1L), artist.getId());
        assertEquals(TEST_NAME, artist.getName());
        assertEquals(TEST_DESCRIPTION, artist.getDescription());
        assertEquals(TEST_LINK, artist.getLink());
        assertArrayEquals(TEST_GENRES, artist.getGenres());
        assertEquals(FORTY_TWO, artist.getTracks());
        assertEquals(FORTY_TWO, artist.getAlbums());
        assertEquals(SMALL, artist.getCover().getSmall());
        assertEquals(BIG, artist.getCover().getBig());
    }
}