package de.vanmar.android.yarrn.util;

import org.androidannotations.api.sharedpreferences.StringPrefField;

public class PrefsUtils {

    public static boolean isSet(final StringPrefField field) {
        return field.exists() && field.get().length() != 0;
    }
}
