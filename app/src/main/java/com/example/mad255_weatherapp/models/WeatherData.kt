package com.example.mad255_weatherapp.models

/*LocationData Class
* holds data from a Json object that was received by an api call.
* */
data class WeatherData(
    val temp: String,
    val feelsLike: String,
    val maxTemp: String,
    val minTemp: String,
    val humidity: String,
    val pressure: String
)