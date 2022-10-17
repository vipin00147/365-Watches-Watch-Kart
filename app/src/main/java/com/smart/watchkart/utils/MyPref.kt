package com.smart.watchkart.utils

import android.content.Context
import android.content.SharedPreferences
import com.smart.watchkart.utils.CONSTANTS

class MyPref {
    private fun getSharedPreference(context: Context): SharedPreferences {
        return context.getSharedPreferences("WATCH_KART", Context.MODE_PRIVATE)
    }

    fun setLoginStatus(context: Context, status: Boolean?) {
        getSharedPreference(context).edit().putBoolean(CONSTANTS.IS_USER_LOGGED, status!!).apply()
    }

    fun getLoginStatus(context: Context): Boolean {
        return getSharedPreference(context).getBoolean(CONSTANTS.IS_USER_LOGGED, false)
    }

    fun setUserName(context: Context, name: String?) {
        getSharedPreference(context).edit().putString(CONSTANTS.USERNAME, name).apply()
    }

    fun getUserName(context: Context): String? {
        return getSharedPreference(context).getString(CONSTANTS.USERNAME, "")
    }

    fun setIsAdmin(context: Context, isAdmin: Boolean?) {
        getSharedPreference(context).edit().putBoolean(CONSTANTS.IS_ADMIN, isAdmin!!).apply()
    }

    fun getIsAdmin(context: Context): Boolean {
        return getSharedPreference(context).getBoolean(CONSTANTS.IS_ADMIN, false)
    }
}