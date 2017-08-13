package com.davitmartirosyan.exp.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preference {

    private static final String PREF_USER_ID = "PREF_USER_ID";

    private static Preference sInstance;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;


    private Preference(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mEditor = mSharedPreferences.edit();
    }

    public static Preference getInstance(Context context) {
        if(sInstance == null) {
            sInstance = new Preference(context);
        }
        return sInstance;
    }

    public void setUserID(long id) {
        mEditor.putLong(PREF_USER_ID, id);
        mEditor.apply();
    }

    public long getUserMail() {
        return mSharedPreferences.getLong(PREF_USER_ID, 0);
    }

}
