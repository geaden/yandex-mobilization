package com.geaden.android.mobilization.app.models;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.geaden.android.mobilization.app.data.Artist;
import com.geaden.android.mobilization.app.util.TestDbUtil;
import com.raizlabs.android.dbflow.runtime.transaction.process.InsertModelTransaction;
import com.raizlabs.android.dbflow.runtime.transaction.process.ProcessModelInfo;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static com.geaden.android.mobilization.app.models.ArtistModelTest.TEST_DESCRIPTION;
import static com.geaden.android.mobilization.app.models.ArtistModelTest.TEST_GENRES;
import static com.geaden.android.mobilization.app.models.ArtistModelTest.TEST_NAME;
import static com.geaden.android.mobilization.app.models.ArtistModelTest.createArtist;
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
    public void setUp() {
        TestDbUtil.cleanupDb();
    }

    @Test
    public void queryArtistsByGenres() {
        Artist newArtist = createArtist(TEST_NAME, TEST_DESCRIPTION, TEST_GENRES);

        List<GenreModel_ArtistModel> genreModelArtistModels = newArtist.toModel()
                .getGenreModelArtistModelList(newArtist.getGenres());

        new InsertModelTransaction<>(ProcessModelInfo.withModels(genreModelArtistModels))
                .onExecute();

        List<GenreModel_ArtistModel> queried = SQLite.select()
                .distinct().from(GenreModel_ArtistModel.class)
                .queryList();

        assertEquals(TEST_GENRES.length, queried.size());

        List<ArtistModel> artists = getArtistByGenres("foo", "bar");

        assertEquals(1, artists.size());

        // Add new artists wit new genre
        Artist anotherArtist = createArtist("zoo", "xoo", new String[]{"metal"});
        genreModelArtistModels = anotherArtist.toModel()
                .getGenreModelArtistModelList(anotherArtist.getGenres());

        new InsertModelTransaction<>(ProcessModelInfo.withModels(genreModelArtistModels))
                .onExecute();

        artists = getArtistByGenres("foo", "metal");

        assertEquals(2, artists.size());
    }

    @Test
    public void bulkInsertRelatedModels() {
        Artist newArtist = createArtist(TEST_NAME, TEST_DESCRIPTION, TEST_GENRES);

        List<GenreModel_ArtistModel> genresArtists = newArtist.toModel().getGenreModelArtistModelList(
                newArtist.getGenres());

        new InsertModelTransaction<>(ProcessModelInfo.withModels(genresArtists)).onExecute();

        // Check that all related models are properly saved.
        List<ArtistModel> artist = SQLite.select().from(ArtistModel.class).queryList();
        assertEquals(1, artist.size());
        List<GenreModel> genres = SQLite.select().from(GenreModel.class).queryList();
        assertEquals(TEST_GENRES.length, genres.size());
        List<GenreModel_ArtistModel> genresArtistsList = SQLite.select().distinct()
                .from(GenreModel_ArtistModel.class).queryList();
        assertEquals(TEST_GENRES.length, genresArtistsList.size());

        // Check that adding new artist with already inserted genre
        // doesn't insert new genre, but inserts new ArtistModel and GenreModel_ArtistModel.
        Artist anotherArtist = createArtist("zoo", "xoo", new String[]{"foo"});
        genresArtists = anotherArtist.toModel().getGenreModelArtistModelList(anotherArtist
                .getGenres());

        new InsertModelTransaction<>(ProcessModelInfo.withModels(genresArtists)).onExecute();

        assertEquals(TEST_GENRES.length, SQLite.select().from(GenreModel.class)
                .queryList().size());
        assertEquals(2, SQLite.select().from(ArtistModel.class)
                .queryList().size());
        assertEquals(3, SQLite.select().from(GenreModel_ArtistModel.class)
                .queryList().size());

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