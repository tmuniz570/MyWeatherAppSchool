package com.tmuniz570.myweatherapp.model.weather

data class Weather(val name: String, val main: Main)

data class Main(val temp: Float)

//Find

data class Find(val count: Int, val list: List<Lista>)

data class Lista(val main: Main, val id: String, val name: String, val weather: List<WeatherList>)

data class WeatherList(val icon: String)