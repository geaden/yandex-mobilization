package com.geaden.android.mobilization.app.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.geaden.android.mobilization.app.R;
import com.geaden.android.mobilization.app.models.ArtistModel;
import com.geaden.android.mobilization.app.models.GenreModel_ArtistModel;
import com.geaden.android.mobilization.app.util.Constants;
import com.geaden.android.mobilization.app.util.Utility;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raizlabs.android.dbflow.runtime.transaction.process.InsertModelTransaction;
import com.raizlabs.android.dbflow.runtime.transaction.process.ProcessModelInfo;

import java.io.IOException;
import java.util.List;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Async Task to load artists from network.
 *
 * @author Gennady Denisov
 */
public class LoadArtistsAsyncTask extends AsyncTask<Context, Void, List<Artist>> {
    private static final String TAG = LoadArtistsAsyncTask.class.getSimpleName();
    private final ArtistsRepository.LoadArtistsCallback mCallback;
    private Context mContext;
    private Gson gson;

    private OkHttpClient mClient;
    private static final String CONTENT_TYPE_LABEL = "Content-Type";
    private static final String CONTENT_TYPE_VALUE_JSON = "application/json; charset=utf-8";

    public LoadArtistsAsyncTask(ArtistsRepository.LoadArtistsCallback callback) {
        mCallback = callback;
    }

    @Override
    protected List<Artist> doInBackground(Context... params) {
        mContext = params[0];

        List<Artist> artists = Lists.newArrayList();

        ConnectivityManager cm = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo ni = cm.getActiveNetworkInfo();

        if (ni == null || !ni.isConnected()) {
            Log.w(TAG, "Not online, not refreshing.");
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt(mContext.getString(R.string.pref_key_load_state),
                    LoadingStatus.NETWORK_ERROR);
            editor.apply();
            return artists;
        }

        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(mContext.getCacheDir(), cacheSize);

        gson = new GsonBuilder()
                .setFieldNamingStrategy(new ArtistFieldNamingStrategy())
                .create();

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();

        mClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .cache(cache)
                .build();


        Request request = new Request.Builder()
                .url(Constants.ARTISTS_URL)
                .addHeader(CONTENT_TYPE_LABEL, CONTENT_TYPE_VALUE_JSON)
                .build();

        try {
            Response response = mClient.newCall(request).execute();

            if (!response.isSuccessful()) {
                Utility.setLoadingStatus(mContext, LoadingStatus.ERROR);
                return artists;
            }

            Artist[] artistsArray = gson.fromJson(response.body().charStream(), Artist[].class);

            List<GenreModel_ArtistModel> genreModelArtistModels = Lists.newArrayList();

            for (Artist artist : artistsArray) {
                ArtistModel artistModel = artist.toModel();
                genreModelArtistModels.addAll(artistModel.getGenreModelArtistModelList(artist.getGenres()));
            }

            // Save retrieved artists to database synchronously.
            new InsertModelTransaction<>(ProcessModelInfo.withModels(genreModelArtistModels)).onExecute();

            // Retrieve list of artists with preferred order.
            List<ArtistModel> models = ArtistsRepositoryImpl.getWhereClause(mContext)
                    .orderBy(ArtistsRepositoryImpl.getOrder(mContext), false)
                    .queryList();

            for (ArtistModel model : models) {
                artists.add(model.toArtist());
            }

            if (artists.size() == 0) {
                Utility.setLoadingStatus(mContext, LoadingStatus.NOT_FOUND);
            }
            return artists;
        } catch (IOException e) {
            Log.e(TAG, "Unexpected exception", e);
            Utility.setLoadingStatus(mContext, LoadingStatus.ERROR);
            return artists;
        }
    }

    @Override
    protected void onPostExecute(List<Artist> artists) {
        super.onPostExecute(artists);
        mCallback.onArtistsLoaded(artists);
    }
}
