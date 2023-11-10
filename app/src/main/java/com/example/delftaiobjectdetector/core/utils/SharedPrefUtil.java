package com.example.delftaiobjectdetector.core.utils;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;

public class SharedPrefUtil {



    private final SharedPreferences sharedPref;

    @Inject public SharedPrefUtil(@ApplicationContext Context context) {

         sharedPref = context.getSharedPreferences("app", Context.MODE_PRIVATE);

    }

    public void increasePermissionRequestCount() {
        int count = sharedPref.getInt("permission_request_count", 0);
        count++;
        sharedPref.edit().putInt("permission_request_count", count).apply();
    }

    public int getPermissionRequestCount() {
        return sharedPref.getInt("permission_request_count", 0);
    }
}
