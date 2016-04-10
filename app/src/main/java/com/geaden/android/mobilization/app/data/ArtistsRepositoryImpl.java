package com.geaden.android.mobilization.app.data;

import android.content.Context;
import android.support.annotation.NonNull;

import com.geaden.android.mobilization.app.models.ArtistModel;
import com.geaden.android.mobilization.app.models.ArtistModel_Table;
import com.raizlabs.android.dbflow.runtime.transaction.TransactionListenerAdapter;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
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
    public void getArtists(@NonNull final LoadArtistCallback callback) {
        checkNotNull(callback);

        // Initialize list of artists.
        final List<Artist> artists = new ArrayList<>(0);

        SQLite.select().from(ArtistModel.class).orderBy(ArtistModel_Table.created_at, true)
                .async().queryList(
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
}
