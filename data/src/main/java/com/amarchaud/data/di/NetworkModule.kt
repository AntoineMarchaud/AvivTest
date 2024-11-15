package com.amarchaud.data.di

import com.amarchaud.data.BuildConfig
import com.amarchaud.data.api.SimpleListDemoApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Singleton
    @Provides
    fun provideClient(
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        val builder = OkHttpClient.Builder().apply {
            if (BuildConfig.DEBUG) {
                addInterceptor(httpLoggingInterceptor)
            }
        }
        return builder.build()
    }


    @Singleton
    @Provides
    fun provideDomainApi(
        okHttpClient: OkHttpClient
    ): SimpleListDemoApi {
        return Retrofit.Builder()
            .baseUrl("https://gsl-apps-technical-test.dignp.com/")
            .addConverterFactory(
                Json.asConverterFactory(
                    "application/json; charset=UTF8".toMediaType()
                )
            )
            .client(okHttpClient)
            .build()
            .create(SimpleListDemoApi::class.java)
    }
}