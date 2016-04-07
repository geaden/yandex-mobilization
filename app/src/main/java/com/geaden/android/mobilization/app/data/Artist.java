package com.geaden.android.mobilization.app.data;

/**
 * Immutable Artist model.
 *
 * @author Gennady Denisov
 */
public final class Artist {
    private final long mId;

    private String mTitle;
    private String mDescription;

    public Artist(long id) {
        mId = id;
    }

    public long getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }
}
