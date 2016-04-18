package com.geaden.android.mobilization.app.models;

import com.geaden.android.mobilization.app.database.ArtistsDatabase;
import com.google.common.collect.Lists;
import com.raizlabs.android.dbflow.annotation.ManyToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.NameAlias;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Where;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Model referenced to Artist Model.
 *
 * @author Gennady Denisov
 */
@ManyToMany(referencedTable = ArtistModel.class, saveForeignKeyModels = true)
@Table(database = ArtistsDatabase.class)
public class GenreModel extends BaseModel {
    @PrimaryKey
    String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Queries artists by array of genres.
     *
     * @param genres array of genres.
     * @return artists that have such genres.
     */
    public static Where<ArtistModel> getArtistsByGenres(String... genres) {
        return SQLite.select(ArtistModel_Table.getAllColumnProperties())
                .distinct()
                .from(ArtistModel.class).as("A")
                .join(GenreModel_ArtistModel.class, Join.JoinType.LEFT_OUTER).as("GA")
                .on(ArtistModel_Table.id.withTable(new NameAlias("A"))
                        .eq(GenreModel_ArtistModel_Table.artistModel_id.withTable(new NameAlias("GA"))))
                .where(GenreModel_ArtistModel_Table.genreModel_name.withTable(new NameAlias("GA"))
                        .in(Lists.newArrayList(genres)));
    }
}
