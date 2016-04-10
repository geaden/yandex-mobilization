package com.geaden.android.mobilization.app.data;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.geaden.android.mobilization.app.models.ArtistModel;
import com.geaden.android.mobilization.app.util.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raizlabs.android.dbflow.runtime.transaction.process.InsertModelTransaction;
import com.raizlabs.android.dbflow.runtime.transaction.process.ProcessModelInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
    private final ArtistsRepository.LoadArtistCallback mCallback;
    private Context mContext;
    private Gson gson;

    private OkHttpClient mClient;
    private static final String CONTENT_TYPE_LABEL = "Content-Type";
    private static final String CONTENT_TYPE_VALUE_JSON = "application/json; charset=utf-8";

    public LoadArtistsAsyncTask(ArtistsRepository.LoadArtistCallback callback) {
        mCallback = callback;
    }

    @Override
    protected List<Artist> doInBackground(Context... params) {
        mContext = params[0];

        List<Artist> artists = new ArrayList<>(0);

        ConnectivityManager cm = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo ni = cm.getActiveNetworkInfo();

        if (ni == null || !ni.isConnected()) {
            Log.w(TAG, "Not online, not refreshing.");
            // TODO: Update state...
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
                throw new IOException("Unexpected code " + response);
            }

            Artist[] artistsArray = gson.fromJson(response.body().charStream(), Artist[].class);

            List<ArtistModel> models = new ArrayList<>();

            // Save retrieved artists to database synchronously
            for (Artist artist : artistsArray) {
                ArtistModel artistModel = artist.toModel();
                artistModel.setCreateAt(new Date());
                models.add(artistModel);
            }
            new InsertModelTransaction<>(ProcessModelInfo.withModels(models)).onExecute();
            return Arrays.asList(artistsArray);
        } catch (IOException e) {
            Log.e(TAG, "Unexpected exception", e);
        }
        return artists;
    }

    @Override
    protected void onPostExecute(List<Artist> artists) {
        super.onPostExecute(artists);
        mCallback.onArtistsLoaded(artists);
    }
}
