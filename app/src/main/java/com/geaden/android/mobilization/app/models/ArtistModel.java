package com.geaden.android.mobilization.app.models;

import com.geaden.android.mobilization.app.data.Artist;
import com.geaden.android.mobilization.app.data.Cover;
import com.geaden.android.mobilization.app.database.ArtistsDatabase;
import com.geaden.android.mobilization.app.util.Constants;
import com.google.common.collect.Lists;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

/**
 * Represents artist database model.
 *
 * @author Gennady Denisov
 */
@Table(database = ArtistsDatabase.class)
public class ArtistModel extends BaseModel {
    private static final String TAG = ArtistModel.class.getSimpleName();
    @PrimaryKey
    long id;

    @Column
    String name;

    @Column
    String description;

    /**
     * While it's possible to get genres from {@link GenreModel_ArtistModel} table
     * by query, it turned out it wasn't good for performance.
     * So this field is required to store genres as string.
     */
    @Column
    String genres;

    @Column
    int albums;

    @Column
    int tracks;

    @Column(name = "cover_small")
    String coverSmall;

    @Column(name = "cover_big")
    String coverBig;

    @Column
    String link;

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public void setAlbums(int albums) {
        this.albums = albums;
    }

    public void setTracks(int tracks) {
        this.tracks = tracks;
    }

    public void setCoverSmall(String coverSmall) {
        this.coverSmall = coverSmall;
    }

    public void setCoverBig(String coverBig) {
        this.coverBig = coverBig;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getGenres() {
        return genres;
    }

    /**
     * Helper method to convert model to artist.
     *
     * @return the Artist.
     */
    public Artist toArtist() {
        Artist artist = new Artist(id, name, description);
        artist.setCover(new Cover(coverSmall, coverBig));
        artist.setTracks(tracks);
        artist.setAlbums(albums);
        artist.setGenres(getGenres().split(Constants.GENRES_SEPARATOR));
        artist.setLink(link);
        return artist;
    }

    /**
     * Gets list of relations between {@link GenreModel} and {@link ArtistModel}.
     *
     * @param genres array of genres that the artist has.
     * @return list of relations for further insertion.
     */
    public List<GenreModel_ArtistModel> getGenreModelArtistModelList(String[] genres) {
        List<GenreModel_ArtistModel> genreModelArtistModels = Lists.newArrayList();
        for (String genre : genres) {
            GenreModel genreModel = new GenreModel();
            genreModel.setName(genre);
            GenreModel_ArtistModel genreModelArtistModel = new GenreModel_ArtistModel();
            genreModelArtistModel.setArtistModel(this);
            genreModelArtistModel.setGenreModel(genreModel);
            genreModelArtistModels.add(genreModelArtistModel);
        }
        return genreModelArtistModels;
    }
}
