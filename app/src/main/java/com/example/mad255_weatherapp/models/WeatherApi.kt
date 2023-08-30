package com.example.mad255_weatherapp.models

import android.util.Log
import org.json.JSONObject
import java.net.URL

class WeatherApi {
    private lateinit var response: String

    //api key stuff: this should be kept secret.
    private val apiKeyWeather = "1db914c623ec2e5caba5ddfe8e12b0de"

    fun callWeatherAPI(lat: String?, lon: String?): WeatherData {
        var latitude = lat
        var longitude = lon
        Log.i("API_debug", "app: calling weatherApi from class.")
        if (latitude == null || longitude == null){
            latitude = "42.24"
            longitude = "88.31"
        }

        //default weather values if api fails
        var weatherData = WeatherData("nil", "nil", "nil", "nil", "nil", "nil")

        //api call
        val apiUrl = "https://api.openweathermap.org/data/2.5/weather?lat=${latitude}&lon=${longitude}&units=imperial&appid=${apiKeyWeather}"
        val result = getResultFromAPI(apiUrl)
        if (result != null) {
            try {
                weatherData = parseWeatherData(result)
            } catch (error: java.lang.Exception) {
                Log.e("locationApi_debug: error", "api-location-api ERROR: $error")
            }
        }
        return weatherData
    }

    private fun parseWeatherData(json: String): WeatherData {
        val jsonObj = JSONObject(json)
        val main = jsonObj.getJSONObject("main")
        return WeatherData(
            main.getString("temp"),
            main.getString("feels_like"),
            main.getString("temp_max"),
            main.getString("temp_min"),
            main.getString("humidity"),
            main.getString("pressure"))
    }
    private fun getResultFromAPI(apiUrl:String): String? {
        Log.i("API_debug", "app: getting result from api")
        try {
            response = URL(apiUrl).readText(Charsets.UTF_8)
        } catch (error: java.lang.Exception) {
            Log.e("api error", "api error: $error")
            return null
        }
        Log.i("API_debug", "app: result success: $response")
        return response
    }
}