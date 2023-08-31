package com.example.mad255_weatherapp.repositories

import android.util.Log
import com.example.mad255_weatherapp.api.WeatherApi
import com.example.mad255_weatherapp.models.WeatherData
import org.json.JSONObject

/*WEATHER REPOSITORY:
* Handles parsing Json objects received by the api
* Handles any errors or problems that may occur during the api call.
* Ensures necessary parameters are recieved for api call.
*
* Returns: null or class: WeatherData
* */
class WeatherRepository(private val weatherApi: WeatherApi) {
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
    fun getWeatherData(lat: String?, lon:String?): WeatherData? {
        if (lat != null && lon != null) {
            val apiResponseJson = weatherApi.callWeatherAPI(lat, lon)
            var weatherData = WeatherData("", "", "", "", "", "")
            if (apiResponseJson != null) {
                try {
                    weatherData = parseWeatherData(apiResponseJson)
                } catch (error: java.lang.Exception) {
                    Log.e("error_debug", "api_weather error: $error")
                    return null //return null if an error occurs during parsing.
                }
            }
            //return parsed weather data to the viewModel if successful.
            return weatherData
        }
        // if lat or lon parameters are missing return null.
        return null
    }
}