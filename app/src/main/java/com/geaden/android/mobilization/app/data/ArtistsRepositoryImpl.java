package com.geaden.android.mobilization.app.data;

import android.content.Context;
import android.support.annotation.NonNull;

import com.geaden.android.mobilization.app.R;
import com.geaden.android.mobilization.app.models.ArtistModel;
import com.geaden.android.mobilization.app.models.ArtistModel_Table;
import com.geaden.android.mobilization.app.models.GenreModel;
import com.geaden.android.mobilization.app.util.Utility;
import com.google.common.collect.Lists;
import com.raizlabs.android.dbflow.runtime.transaction.TransactionListenerAdapter;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.property.IProperty;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Implementation of {@link ArtistsRepository} to load artists from network.
 *
 * @author Gennady Denisov
 */
public class ArtistsRepositoryImpl implements ArtistsRepository {
    private static final String TAG = ArtistsRepositoryImpl.class.getSimpleName();

    private Context mContext;


    public ArtistsRepositoryImpl(Context context) {
        mContext = context;
    }

    @Override
    public void getArtists(@NonNull final LoadArtistsCallback callback) {
        checkNotNull(callback);

        Utility.setLoadingStatus(mContext, LoadingStatus.LOADING);

        SQLite.select()
                .from(ArtistModel.class)
                .orderBy(getOrder(mContext), false)
                .async()
                .queryList(
                        new TransactionListenerAdapter<List<ArtistModel>>() {
                            @Override
                            public void onResultReceived(List<ArtistModel> models) {
                                super.onResultReceived(models);
                                if (models.size() > 0) {
                                    processArtistModels(models, callback);
                                    return;
                                }
                                // No entries in the database, call Yandex.
                                new LoadArtistsAsyncTask(callback).execute(mContext);
                            }
                        });
    }


    @Override
    public void refreshData() {
        SQLite.delete(ArtistModel.class).async().execute();
        SQLite.delete(GenreModel.class).async().execute();
    }

    @Override
    public void getArtist(@NonNull long artistId, @NonNull final GetArtistCallback callback) {
        checkNotNull(artistId);
        checkNotNull(callback);
        SQLite.select()
                .from(ArtistModel.class)
                .where(ArtistModel_Table.id.eq(artistId))
                .async()
                .querySingle(new TransactionListenerAdapter<ArtistModel>() {
                    @Override
                    public void onResultReceived(ArtistModel artistModel) {
                        if (null != artistModel) {
                            callback.onArtistLoaded(artistModel.toArtist());
                        } else {
                            callback.onArtistLoaded(null);
                        }
                    }
                });
    }

    @Override
    public void findArtistsByName(@NonNull String query, @NonNull final LoadArtistsCallback callback) {
        checkNotNull(query);
        checkNotNull(callback);

        Utility.setLoadingStatus(mContext, LoadingStatus.LOADING);

        SQLite.select()
                .from(ArtistModel.class)
                .where(ArtistModel_Table.name.like('%' + query.toLowerCase() + '%'))
                .orderBy(getOrder(mContext), false)
                .async()
                .queryList(new TransactionListenerAdapter<List<ArtistModel>>() {
                    @Override
                    public void onResultReceived(List<ArtistModel> models) {
                        super.onResultReceived(models);
                        processArtistModels(models, callback);
                    }
                });
    }

    /**
     * Gets property to order artists by.
     *
     * @param ctx the Context to get SharedPreferences.
     * @return order property.
     */
    public static IProperty getOrder(Context ctx) {
        String currentOrder = Utility.getPreferredOrder(ctx);
        if (currentOrder.equals(ctx.getString(R.string.pref_order_by_tracks))) {
            return ArtistModel_Table.tracks;
        } else {
            return ArtistModel_Table.albums;
        }
    }

    @Override
    public void findArtistsByGenres(@NonNull String[] genres, @NonNull final LoadArtistsCallback callback) {
        Utility.setLoadingStatus(mContext, LoadingStatus.LOADING);

        GenreModel.getArtistsByGenres(genres)
                .orderBy(getOrder(mContext), false)
                .async().queryList(new TransactionListenerAdapter<List<ArtistModel>>() {
            @Override
            public void onResultReceived(List<ArtistModel> models) {
                super.onResultReceived(models);
                processArtistModels(models, callback);
            }
        });
    }

    /**
     * Helper method to process result of retrieved models.
     *
     * @param models   retrieved models.
     * @param callback loading callback.
     */
    private void processArtistModels(List<ArtistModel> models, LoadArtistsCallback callback) {
        List<Artist> artists = Lists.newArrayList();
        for (ArtistModel model : models) {
            artists.add(model.toArtist());
        }
        if (artists.size() == 0) {
            Utility.setLoadingStatus(mContext, LoadingStatus.NOT_FOUND);
        }
        callback.onArtistsLoaded(artists);
    }

    @Override
    public void getGenres(@NonNull final LoadGenresCallback callback) {
        checkNotNull(callback);
        SQLite.select().distinct()
                .from(GenreModel.class)
                .async()
                .queryList(new TransactionListenerAdapter<List<GenreModel>>() {
                    @Override
                    public void onResultReceived(List<GenreModel> genreModels) {
                        super.onResultReceived(genreModels);
                        String[] genres = new String[genreModels.size()];
                        int i = 0;
                        for (GenreModel genreModel : genreModels) {
                            genres[i++] = genreModel.getName();
                        }
                        callback.onGenresLoaded(genres);
                    }
                });
    }
}
