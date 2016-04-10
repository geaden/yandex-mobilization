package com.geaden.android.mobilization.app.database;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Database to hold artists records.
 *
 * @author Gennady Denisov
 */
@Database(name = ArtistsDatabase.NAME, version = ArtistsDatabase.VERSION)
public class ArtistsDatabase {
    public static final String NAME = "artists";

    public static final int VERSION = 1;
}
