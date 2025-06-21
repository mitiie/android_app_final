package com.example.androidappfinal.helpers;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_USER_ID = "userId";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void createLoginSession(String userId) {
        editor.putString(KEY_USER_ID, userId);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.contains(KEY_USER_ID);
    }

    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}
