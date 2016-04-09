package com.geaden.android.mobilization.app.data;

import com.google.gson.GsonBuilder;

import org.junit.Test;

import java.io.FileReader;
import java.io.Reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for Artist model.
 *
 * @author Gennady Denisov
 */
public class ArtistTest {
    @Test
    public void shouldGetListOfArtistsFromJson() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        Reader reader = new FileReader(classLoader.getResource("artists.json").getFile()
                .replaceAll("%20", "\\ "));  // Local fix of file path.
        Artist[] artists = new GsonBuilder()
                .setFieldNamingStrategy(new ArtistFieldNamingStrategy())
                .create()
                .fromJson(reader, Artist[].class);
        assertEquals(artists.length, 317);
        for (Artist artist : artists) {
            assertNotNull(artist.getId());
            assertNotNull(artist.getDescription());
            assertNotNull(artist.getCover());
            assertNotNull(artist.getCover().getBig());
            assertNotNull(artist.getCover().getSmall());
            assertNotNull(artist.getAlbums());
            assertNotNull(artist.getTracks());
        }
    }
}