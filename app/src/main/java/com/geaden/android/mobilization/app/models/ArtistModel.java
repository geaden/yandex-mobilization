package com.geaden.android.mobilization.app.models;

import com.geaden.android.mobilization.app.data.Artist;
import com.geaden.android.mobilization.app.data.Cover;
import com.geaden.android.mobilization.app.database.ArtistsDatabase;
import com.geaden.android.mobilization.app.util.Constants;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Represents artist database model.
 *
 * @author Gennady Denisov
 */
@ModelContainer
@Table(database = ArtistsDatabase.class)
public class ArtistModel extends BaseModel {
    @PrimaryKey
    long id;

    @Column
    String name;

    @Column
    String description;

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
        artist.setGenres(genres.split(Constants.GENRES_SEPARATOR));
        artist.setLink(link);
        return artist;
    }
}
