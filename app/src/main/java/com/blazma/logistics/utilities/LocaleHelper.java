package com.blazma.logistics.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;

import java.util.Locale;

public class LocaleHelper {

    private static final String SELECTED_LANGUAGE = "Locale.Helper.Selected.Language";

    public static void onAttach(Context context) {
//        String lang = getPersistedData(context, "ar");
//        if(lang.equals("en")){
//            changeLocale(context, lang, "US");
//        }else{
//            changeLocale(context, lang, "SA");
//        }
    }

    public static void onAttach(Context context, String defaultLanguage, String country) {
        String lang = getPersistedData(context, defaultLanguage);
        changeLocale(context, lang, country);
    }

    public static String getLanguage(Context context) {
        return getPersistedData(context, "en");
    }

    public static String getPersistedData(Context context, String defaultLanguage) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(SELECTED_LANGUAGE, defaultLanguage);
    }

    private static void persist(Context context, String language) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(SELECTED_LANGUAGE, language);
        editor.apply();
    }

    // Change locale
    public static void changeLocale(Context context, String localeCode, String countryCode){

        persist(context, localeCode);

        Locale locale = new Locale(localeCode, countryCode);
        Resources res = context.getResources();

        // Change locale settings in the app.
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.setLocale(locale); // API 17+ only.

        res.updateConfiguration(conf, dm);
    }

}
