package com.example.zybanking.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {

    private static final String PREF_NAME = "auth_prefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // ===== ACCESS TOKEN =====
    public static void saveAccessToken(Context context, String token) {
        getPrefs(context).edit()
                .putString(KEY_ACCESS_TOKEN, token)
                .apply();
    }

    public static String getAccessToken(Context context) {
        return getPrefs(context).getString(KEY_ACCESS_TOKEN, null);
    }

    public static void clearAccessToken(Context context) {
        getPrefs(context).edit()
                .remove(KEY_ACCESS_TOKEN)
                .apply();
    }

    // ===== REFRESH TOKEN =====
    public static void saveRefreshToken(Context context, String token) {
        getPrefs(context).edit()
                .putString(KEY_REFRESH_TOKEN, token)
                .apply();
    }

    public static String getRefreshToken(Context context) {
        return getPrefs(context).getString(KEY_REFRESH_TOKEN, null);
    }

    public static void clearAll(Context context) {
        getPrefs(context).edit().clear().apply();
    }
}
