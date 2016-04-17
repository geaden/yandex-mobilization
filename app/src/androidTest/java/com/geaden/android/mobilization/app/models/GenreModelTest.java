package com.geaden.android.mobilization.app.models;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.geaden.android.mobilization.app.data.Artist;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static com.geaden.android.mobilization.app.models.ArtistModelTest.TEST_DESCRIPTION;
import static com.geaden.android.mobilization.app.models.ArtistModelTest.TEST_GENRES;
import static com.geaden.android.mobilization.app.models.ArtistModelTest.TEST_NAME;
import static com.geaden.android.mobilization.app.models.ArtistModelTest.saveArtist;
import static org.junit.Assert.assertEquals;

/**
 * Test to get artists by list of genres.
 *
 * @author Gennady Denisov
 */
@SmallTest
@RunWith(AndroidJUnit4.class)
public class GenreModelTest {

    @Before
    public void cleanupDb() {
        SQLite.delete(ArtistModel.class).query();
        SQLite.delete(GenreModel.class).query();
        SQLite.delete(GenreModel_ArtistModel.class).query();
    }

    @Test
    public void queryArtistsByGenres() {
        Artist newArtist = saveArtist(TEST_NAME, TEST_DESCRIPTION, TEST_GENRES);

        newArtist.saveGenres(false);

        List<GenreModel_ArtistModel> queried = SQLite.select()
                .distinct().from(GenreModel_ArtistModel.class)
                .queryList();

        assertEquals(2, queried.size());

        List<ArtistModel> artists = getArtistByGenres("foo", "bar");

        assertEquals(1, artists.size());

        // Add new artists wit new genre
        Artist anotherArtist = saveArtist("zoo", "xoo", new String[]{"metal"});

        anotherArtist.saveGenres(false);

        artists = getArtistByGenres("foo", "metal");

        assertEquals(2, artists.size());
    }

    /**
     * Query artist by genres.
     *
     * @param genres list of genres.
     * @return artists that have such genres.
     */
    private static List<ArtistModel> getArtistByGenres(String... genres) {
        return GenreModel.getArtistsByGenres(genres)
                .queryList();
    }
}