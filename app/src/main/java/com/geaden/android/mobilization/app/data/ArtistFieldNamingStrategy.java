package com.geaden.android.mobilization.app.data;

import com.google.gson.FieldNamingStrategy;

import java.lang.reflect.Field;

/**
 * Custom field naming strategy to correctly parse artists response.
 *
 * @author Gennady Denisov
 */
public class ArtistFieldNamingStrategy implements FieldNamingStrategy {

    @Override
    public String translateName(Field f) {
        String fieldName = f.getName();
        // Cut off "m" letter.
        return (fieldName.substring(1, fieldName.length())).toLowerCase();
    }
}
