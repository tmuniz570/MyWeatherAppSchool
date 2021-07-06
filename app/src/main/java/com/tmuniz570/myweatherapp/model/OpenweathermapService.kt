package com.tmuniz570.myweatherapp.model

import com.tmuniz570.myweatherapp.model.weather.Find
import com.tmuniz570.myweatherapp.model.weather.Lista
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenweathermapService {

    @GET("data/2.5/weather")
    fun listWeather(
        @Query("id") id: String,
        @Query("units") units: String,
        @Query("appid") appid: String
    ): Call<Lista>

    @GET("data/2.5/find")
    fun listFind(
        @Query("q") q: String,
        @Query("units") units: String,
        @Query("appid") appid: String
    ): Call<Find>

}

//     https://api.openweathermap.org/data/2.5/find?q=maceio&APPID=c80b07d002800e502f16cf21b7a1f545&units=metric