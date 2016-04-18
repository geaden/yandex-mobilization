package com.geaden.android.mobilization.app.data;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import com.geaden.android.mobilization.app.models.ArtistModel;
import com.geaden.android.mobilization.app.models.GenreModel;
import com.geaden.android.mobilization.app.models.GenreModel_ArtistModel;
import com.geaden.android.mobilization.app.util.Constants;
import com.geaden.android.mobilization.app.util.TestDbUtil;
import com.geaden.android.mobilization.app.util.Utility;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static org.junit.Assert.assertEquals;

/**
 * Tests for loading artists from network.
 *
 * @author Gennady Denisov
 */
@MediumTest
@RunWith(AndroidJUnit4.class)
public class LoadArtistsAsyncTaskTest {
    private MockWebServer mServer;

    @Before
    public void mockServer() throws Exception {
        TestDbUtil.cleanupDb();
        // Reset filter genres.
        Utility.setFilterGenres(InstrumentationRegistry.getTargetContext(), new String[0]);
        mServer = new MockWebServer();
        mServer.start();
        Constants.ARTISTS_URL = mServer.url("/").toString();
    }

    @Test
    public void loadArtistsFromNetworkAndSaveInDb() throws Exception {
        assertEquals(0, SQLite.select().from(ArtistModel.class).queryList().size());
        mServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(getStringFromFile(InstrumentationRegistry.getContext(),
                        "mock_artists.json")));

        new LoadArtistsAsyncTask(new ArtistsRepository.LoadArtistsCallback() {
            @Override
            public void onArtistsLoaded(List<Artist> artists) {
                // Do nothing...
            }
        }).execute(InstrumentationRegistry.getInstrumentation().getTargetContext()).get();

        // Check that we store data in database.
        List<ArtistModel> models = SQLite.select().from(ArtistModel.class).queryList();
        assertEquals(3, models.size());

        List<GenreModel> genres = SQLite.select().from(GenreModel.class).queryList();
        assertEquals(5, genres.size());

        List<GenreModel_ArtistModel> genresArtists = SQLite.select()
                .from(GenreModel_ArtistModel.class).queryList();
        assertEquals(9, genresArtists.size());
    }

    /**
     * Helper method to get file content by file name.
     *
     * @param context  the Context to get resources from.
     * @param filePath the file path to read.
     * @return content of the file.
     * @throws Exception
     */
    public static String getStringFromFile(Context context, String filePath) throws Exception {
        final InputStream is = context.getResources().getAssets().open(filePath);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        final StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        is.close();
        return sb.toString();
    }

    @After
    public void tearDown() throws Exception {
        mServer.shutdown();
    }
}