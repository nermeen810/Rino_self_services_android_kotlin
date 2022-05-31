package com.rino.self_services.di.module

import android.content.Context
import com.rino.self_services.model.dataSource.remoteDataSource.ApiService
import com.rino.self_services.model.dataSource.NetworkConnectionInterceptor
import com.rino.self_services.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class RetrofitModule {

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {

        return Retrofit.Builder()
            .baseUrl(Constants.base_url)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideRestApi(retrofit: Retrofit) : ApiService {
        return  retrofit.create(ApiService::class.java)
    }

    @Provides
    fun provideClient(@ApplicationContext context: Context) : OkHttpClient {

        return OkHttpClient.Builder()
            .addInterceptor(NetworkConnectionInterceptor(context))
            .build()
    }


}