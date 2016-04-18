package com.geaden.android.mobilization.app.util;

import com.geaden.android.mobilization.app.models.ArtistModel;
import com.geaden.android.mobilization.app.models.GenreModel;
import com.geaden.android.mobilization.app.models.GenreModel_ArtistModel;
import com.raizlabs.android.dbflow.sql.language.SQLite;

/**
 * Test database utility class.
 *
 * @author Gennady Denisov
 */
public final class TestDbUtil {
    private TestDbUtil() {

    }

    public static void cleanupDb() {
        SQLite.delete(ArtistModel.class).query();
        SQLite.delete(GenreModel.class).query();
        SQLite.delete(GenreModel_ArtistModel.class).query();
    }
}
