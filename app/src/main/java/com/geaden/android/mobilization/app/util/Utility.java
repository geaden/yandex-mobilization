package com.geaden.android.mobilization.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.geaden.android.mobilization.app.R;
import com.geaden.android.mobilization.app.data.LoadingStatus;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility methods.
 *
 * @author Gennady Denisov
 */
public final class Utility {
    private Utility() {

    }

    /**
     * Gets preferred sort order from user settings.
     *
     * @param ctx the context to get SharedPreferences from.
     * @return the preferred sort order.
     */
    static public String getPreferredOrder(Context ctx) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        return sp.getString(ctx.getString(R.string.pref_key_order_value),
                ctx.getString(R.string.pref_default_order));
    }

    /**
     * Sets order of artists.
     *
     * @param ctx   the Context to get SharedPreferences.
     * @param order sorting order to be set.
     */
    public static void setPreferredOrder(Context ctx, String order) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(ctx.getString(R.string.pref_key_order_value), order);
        ed.apply();
    }

    /**
     * Gets filtered genres.
     *
     * @param ctx the Context to get SharedPreferences.
     * @return array of genres.
     */
    public static String[] getFilterGenres(Context ctx) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        Set<String> genresSet = sp.getStringSet(ctx.getString(R.string.pref_key_filter_genres), null);
        if (genresSet == null) {
            return new String[0];
        }
        return genresSet.toArray(new String[genresSet.size()]);
    }

    /**
     * Sets filtered genres.
     *
     * @param ctx    the Context to get SharedPreferences.
     * @param genres genres to set.
     */
    public static void setFilterGenres(Context ctx, String[] genres) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor ed = sp.edit();
        ed.putStringSet(ctx.getString(R.string.pref_key_filter_genres),
                new HashSet<String>(Arrays.asList(genres)));
        ed.apply();
    }


    /**
     * Gets the loading status.
     *
     * @param ctx the Context to get Preferences from.
     * @return the loading status.
     */
    @SuppressWarnings("ResourceType")
    @LoadingStatus
    public static int getLoadingStatus(Context ctx) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        return sp.getInt(ctx.getString(R.string.pref_key_load_state),
                LoadingStatus.ERROR);
    }

    /**
     * Sets loading status.
     *
     * @param ctx        the Context to get SharedPreferences from.
     * @param loadStatus value of loading status.
     */
    public static void setLoadingStatus(Context ctx, @LoadingStatus int loadStatus) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor ed = sp.edit();
        ed.putInt(ctx.getString(R.string.pref_key_load_state), loadStatus);
        ed.commit();
    }
}
