package com.rino.self_services.dataSource.localDataSource

import android.content.SharedPreferences

class MySharedPreference (private val sharedPreference: SharedPreferences) {

    fun setBoolean (key: String,value: Boolean = false ){
        sharedPreference.edit().putBoolean(key,value)?.apply()
    }

    fun getBoolean(key: String,defualtValue: Boolean = false):Boolean{
        return sharedPreference.getBoolean(key,defualtValue)?:false
    }
    fun setString (key: String,value: String ){
        sharedPreference.edit().putString(key,value)?.apply()
    }

    fun getString(key: String):String{
        return sharedPreference.getString(key,"")?:""
    }
}