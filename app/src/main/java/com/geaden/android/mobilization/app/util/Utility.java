package com.geaden.android.mobilization.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.geaden.android.mobilization.app.R;

/**
 * {}.
 *
 * @author Gennady Denisov
 */
public final class Utility {
    private Utility() {

    }

    /**
     * Gets preferred sort order from user settings
     *
     * @param c the context to get SharedPreferences from
     * @return the preferred sort order
     */
    static public String getPreferredOrder(Context c) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        return sp.getString(c.getString(R.string.pref_key_order_value),
                c.getString(R.string.pref_default_order));
    }

    /**
     * Sets order of artists.
     *
     * @param ctx   the Context to get SharedPreferences
     * @param order sorting order to be set
     */
    public static void setOrder(Context ctx, String order) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(ctx.getString(R.string.pref_key_order_value), order);
        ed.apply();
    }
}
