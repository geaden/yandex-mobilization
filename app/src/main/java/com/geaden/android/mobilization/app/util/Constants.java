package com.geaden.android.mobilization.app.util;

/**
 * Holder for constants used in the app.
 *
 * @author Gennady Denisov
 */
public final class Constants {

    private Constants() {
    }

    public static final String YANDEX_MOBILIZATION_LINK = "https://yandex.ru/mobilization/";

    // Explicitly not set to final, as needs to be changed during testing.
    public static String ARTISTS_URL = "http://cache-default06g.cdn.yandex.net/download.cdn.yandex.net/mobilization-2016/artists.json";

    public static final String GENRES_SEPARATOR = ", ";
}
