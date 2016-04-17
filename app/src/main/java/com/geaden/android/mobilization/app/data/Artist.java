package com.geaden.android.mobilization.app.data;

import com.geaden.android.mobilization.app.models.ArtistModel;
import com.geaden.android.mobilization.app.models.GenreModel;
import com.geaden.android.mobilization.app.models.GenreModel_ArtistModel;
import com.geaden.android.mobilization.app.util.Constants;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.raizlabs.android.dbflow.runtime.TransactionManager;
import com.raizlabs.android.dbflow.runtime.transaction.process.InsertModelTransaction;
import com.raizlabs.android.dbflow.runtime.transaction.process.ProcessModelInfo;

import java.util.List;

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
        artistModel.setGenres(Joiner.on(Constants.GENRES_SEPARATOR).skipNulls().join(mGenres));
        artistModel.setLink(mLink);
        artistModel.setAlbums(mAlbums);
        artistModel.setTracks(mTracks);
        return artistModel;
    }

    /**
     * Saves genres belonging to the artist to the databases.
     *
     * @param async if save should performed in async mode.
     */
    public void saveGenres(boolean async) {
        List<GenreModel_ArtistModel> genreModelArtistModels = Lists.newArrayList();
        for (String genre : getGenres()) {
            GenreModel genreModel = new GenreModel();
            genreModel.setName(genre);
            genreModel.save();
            GenreModel_ArtistModel genreModelArtistModel = new GenreModel_ArtistModel();
            genreModelArtistModel.setArtistModel(toModel());
            genreModelArtistModel.setGenreModel(genreModel);
            genreModelArtistModels.add(genreModelArtistModel);
        }
        if (async) {
            TransactionManager.getInstance().saveOnSaveQueue(genreModelArtistModels);
            return;
        }
        new InsertModelTransaction<>(ProcessModelInfo.withModels(genreModelArtistModels)).onExecute();
    }
}
