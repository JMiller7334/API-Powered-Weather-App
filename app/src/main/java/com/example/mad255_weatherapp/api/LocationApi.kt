package com.example.mad255_weatherapp.api

import android.util.Log
import com.example.mad255_weatherapp.BuildConfig
import java.net.URL


/*LOCATION API
* Api class that makes api calls
* * Returns: JsonObject or null if api call fails.
* */
class LocationApi {
    private lateinit var response: String
    private val apiKeyLocation = BuildConfig.LOCATION_API_KEY
    fun callLocationAPI(zipCode: String): String? {
        Log.i("locationApi_debug", "api-location: getting location...")
        val apiUrl = "https://app.zipcodebase.com/api/v1/search?apikey=${apiKeyLocation}&codes=${zipCode}&country=us"
        return getResultFromAPI(apiUrl)
    }

    private fun getResultFromAPI(apiUrl:String): String? {
        try {
            response = URL(apiUrl).readText(Charsets.UTF_8)
        } catch (error: java.lang.Exception) {
            Log.e("error_debug", "api_location error: $error")
            return null
        }
        return response
    }
}