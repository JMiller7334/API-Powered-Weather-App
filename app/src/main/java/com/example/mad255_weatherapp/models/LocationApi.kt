package com.example.mad255_weatherapp.models

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class LocationApi {
    private lateinit var response: String

    private val apiKeyLocation = "1622e930-ac9d-11ed-a940-ffdae417e345"
    suspend fun callLocationAPI(zipCode: String): LocationData{
        Log.i("locationApi_debug", "api-location: getting location...")
        val apiUrl = "https://app.zipcodebase.com/api/v1/search?apikey=${apiKeyLocation}&codes=${zipCode}&country=us"
        val result = getResultFromAPI(apiUrl)
            var locationData = LocationData("nil", "nil", "nil", "nil")
            if (result != null) {
                try {
                    locationData = parseLocationData(result, zipCode)
                } catch (error: java.lang.Exception) {
                    Log.e("locationApi_debug: error", "api-location-api ERROR: $error")
                }
            }
        return locationData
    }

    private suspend fun parseLocationData(incomingJSON: String, zipCode: String): LocationData {
        Log.i("locationApi_debug", "api-location: converting location info")
        val locationData = LocationData("nil", "nil", "nil", "nil")
        withContext(Dispatchers.Main) {
            val jsonObj = JSONObject(incomingJSON)
            val main = jsonObj.getJSONObject("results") //defined as a Json Object
            val zipData = main.getJSONArray(zipCode) //this is defined as a JSON array

            for (i in 0 until zipData.length()) {
                locationData.longitude = zipData.getJSONObject(i).getString("longitude")
                locationData.latitude = zipData.getJSONObject(i).getString("latitude")
                locationData.state = zipData.getJSONObject(i).getString("state")
                locationData.city = zipData.getJSONObject(i).getString("city")
            }
        }
        return locationData
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