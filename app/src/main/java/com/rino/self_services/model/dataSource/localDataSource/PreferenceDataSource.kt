package com.rino.self_services.model.dataSource.localDataSource

import com.rino.self_services.utils.*


open class PreferenceDataSource(private val sharedPreference: MySharedPreference): Preference {
    override fun getFirstTimeLaunch(): Boolean {
        return sharedPreference.getBoolean(FIRST_TIME_LAUNCH_KEY,true)
    }

    override fun setFirstTimeLaunch(firstTimeLaunch: Boolean) {
        return sharedPreference.setBoolean(FIRST_TIME_LAUNCH_KEY,firstTimeLaunch)
    }

    override fun isLogin(): Boolean {
        return sharedPreference.getBoolean(LOGIN_KEY)
    }

    override fun setLogin(login: Boolean) {
        return sharedPreference.setBoolean(LOGIN_KEY,login)
    }

    override fun setToken(token: String) {
        return sharedPreference.setString(TOKEN_KEY,token)
    }

    override fun getToken(): String {
        return sharedPreference.getString(TOKEN_KEY)
    }

    override fun setRefreshToken(refreshToken: String) {
        return sharedPreference.setString(REFRESH_TOKEN_KEY,refreshToken)
    }

    override fun getRefreshToken(): String {
        return sharedPreference.getString(REFRESH_TOKEN_KEY)
    }
   override  fun logout() {
      setLogin(false)
      setToken("")
      setRefreshToken("")

    }

}