package com.geaden.android.mobilization.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.geaden.android.mobilization.app.R;
import com.geaden.android.mobilization.app.data.LoadingStatus;

/**
 * {}.
 *
 * @author Gennady Denisov
 */
public final class Utility {
    private Utility() {

    }

    /**
     * Gets preferred sort order from user settings.
     *
     * @param c the context to get SharedPreferences from.
     * @return the preferred sort order.
     */
    static public String getPreferredOrder(Context c) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        return sp.getString(c.getString(R.string.pref_key_order_value),
                c.getString(R.string.pref_default_order));
    }

    /**
     * Sets order of artists.
     *
     * @param ctx   the Context to get SharedPreferences.
     * @param order sorting order to be set.
     */
    public static void setOrder(Context ctx, String order) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(ctx.getString(R.string.pref_key_order_value), order);
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
