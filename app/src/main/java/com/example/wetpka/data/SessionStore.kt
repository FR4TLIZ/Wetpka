package com.example.wetpka.data

import android.content.Context

object SessionStore {
    private const val PREFS_NAME = "auth"
    private const val KEY_LOGGED_IN_USER_ID = "logged_in_user_id"

    fun saveLoggedInUserId(context: Context, userId: Int) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putInt(KEY_LOGGED_IN_USER_ID, userId)
            .apply()
    }

    fun getLoggedInUserId(context: Context): Int {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getInt(KEY_LOGGED_IN_USER_ID, -1)
    }

    fun clearLoggedInUser(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_LOGGED_IN_USER_ID)
            .apply()
    }
}

