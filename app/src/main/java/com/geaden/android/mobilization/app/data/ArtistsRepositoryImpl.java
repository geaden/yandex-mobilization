package com.geaden.android.mobilization.app.data;

import android.content.Context;
import android.support.annotation.NonNull;

import com.geaden.android.mobilization.app.R;
import com.geaden.android.mobilization.app.models.ArtistModel;
import com.geaden.android.mobilization.app.models.ArtistModel_Table;
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

        // Initialize list of artists.
        final List<Artist> artists = Lists.newArrayList();

        SQLite.select()
                .from(ArtistModel.class)
                .orderBy(getOrder(), false)
                .async()
                .queryList(
                        new TransactionListenerAdapter<List<ArtistModel>>() {
                            @Override
                            public void onResultReceived(List<ArtistModel> models) {
                                super.onResultReceived(models);
                                if (models.size() > 0) {
                                    for (ArtistModel model : models) {
                                        artists.add(model.toArtist());
                                    }
                                    callback.onArtistsLoaded(artists);
                                } else {
                                    new LoadArtistsAsyncTask(callback).execute(mContext);
                                }
                            }
                        });
    }


    @Override
    public void refreshData() {
        SQLite.delete(ArtistModel.class).async().execute();
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
        SQLite.select()
                .from(ArtistModel.class)
                .where(ArtistModel_Table.name.like('%' + query.toLowerCase() + '%'))
                .orderBy(getOrder(), false)
                .async()
                .queryList(new TransactionListenerAdapter<List<ArtistModel>>() {
                    @Override
                    public void onResultReceived(List<ArtistModel> models) {
                        super.onResultReceived(models);
                        List<Artist> artists = Lists.newArrayList();
                        for (ArtistModel model : models) {
                            artists.add(model.toArtist());
                        }
                        callback.onArtistsLoaded(artists);
                    }
                });
    }

    /**
     * Gets property to order artists by.
     *
     * @return order property.
     */
    private IProperty getOrder() {
        String currentOrder = Utility.getPreferredOrder(mContext);
        if (currentOrder.equals(mContext.getString(R.string.pref_order_by_tracks))) {
            return ArtistModel_Table.tracks;
        } else {
            return ArtistModel_Table.albums;
        }
    }

    @Override
    public void findArtistsByGenres(@NonNull String[] genres, @NonNull LoadArtistsCallback callback) {

    }
}
