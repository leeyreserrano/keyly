package com.example.keyly_projecte_intermodular.gestions;

import android.content.Context;
import android.content.res.Configuration;

import java.util.Locale;

public class GestionsIdiomes {
    private static final String PREFS_NAME = "prefs";
    private static final String KEY_LANG   = "lang";

    public static void canviarIdioma(Context context, String idioma) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_LANG, idioma)
                .apply();
    }

    public static String obtenirIdioma(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString(KEY_LANG, "ca"); // Idioma per defecte català
    }

    /**
     * Aplicar l'idioma triat a la configuració de l'aplicació
     * @param context
     * @return
     */
    public static Context aplicarIdioma(Context context) {
        String lang = obtenirIdioma(context);

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Configuration config = new Configuration(context.getResources().getConfiguration());
        config.setLocale(locale);

        return context.createConfigurationContext(config);
    }
}
