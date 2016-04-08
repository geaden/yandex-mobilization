package com.geaden.android.mobilization.app.data;

import java.util.List;

/**
 * Immutable Artist model.
 *
 * @author Gennady Denisov
 */
public final class Artist {
    private final long mId;
    private String mName;
    private String mDescription;
    private int mTracks;
    private int mAlbums;
    private String mLink;
    private List<String> mGenres;
    private Cover mCover;


    public Artist(long id, String name, String description) {
        mId = id;
        mName = name;
        mDescription = description;
    }

    public long getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getDescription() {
        return mDescription;
    }

    public int getTracks() {
        return mTracks;
    }

    public int getAlbums() {
        return mAlbums;
    }

    public List<String> getGenres() {
        return mGenres;
    }

    public String getLink() {
        return mLink;
    }

    public Cover getCover() {
        return mCover;
    }

    public void setTracks(int tracks) {
        mTracks = tracks;
    }

    public void setLink(String link) {
        mLink = link;
    }

    public void setAlbums(int albums) {
        mAlbums = albums;
    }

    public void setGenres(List<String> genres) {
        mGenres = genres;
    }

    public void setCover(Cover cover) {
        mCover = cover;
    }
}
