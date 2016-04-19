package com.geaden.android.mobilization.app.data;

import com.geaden.android.mobilization.app.models.ArtistModel;
import com.geaden.android.mobilization.app.util.Constants;
import com.google.common.base.Joiner;

/**
 * Immutable Artist model.
 *
 * @author Gennady Denisov
 */
public final class Artist {
    private final Long mId;
    private String mName;
    private String mDescription;
    private Integer mTracks;
    private Integer mAlbums;
    private String mLink;
    private String[] mGenres;
    private Cover mCover;

    public Artist(Long id, String name, String description) {
        mId = id;
        mName = name;
        mDescription = description;
    }

    public Long getId() {
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

    public String[] getGenres() {
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

    public void setGenres(String[] genres) {
        mGenres = genres;
    }

    public void setCover(Cover cover) {
        mCover = cover;
    }

    @Override
    public String toString() {
        return "Artist{" + mId +
                ", " + mName +
                ", " + mDescription +
                "}";
    }

    /**
     * Helper method to convert to model class.
     *
     * @return the instance of {@link ArtistModel}
     */
    public ArtistModel toModel() {
        ArtistModel artistModel = new ArtistModel();
        artistModel.setId(mId);
        artistModel.setName(mName);
        artistModel.setDescription(mDescription);
        artistModel.setCoverBig(mCover.getBig());
        artistModel.setCoverSmall(mCover.getSmall());
        artistModel.setGenres(Joiner.on(Constants.GENRES_SEPARATOR).join(mGenres));
        artistModel.setLink(mLink);
        artistModel.setAlbums(mAlbums);
        artistModel.setTracks(mTracks);
        return artistModel;
    }
}
