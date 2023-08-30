package com.example.mad255_weatherapp.repositories

import android.util.Log
import com.example.mad255_weatherapp.api.LocationApi
import com.example.mad255_weatherapp.models.LocationData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class LocationRepository(private val locationApi: LocationApi) {

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
    suspend fun getLocationData(zipCode: String): LocationData {
        val apiResponse = locationApi.callLocationAPI(zipCode)
        var locationData = LocationData(null, null, "Unknown", "Unknown")
        if (apiResponse != null) {
            try {
                locationData = parseLocationData(apiResponse, zipCode)
            } catch (error: java.lang.Exception) {
                Log.e("locationRepository_debug: error", "repository-location ERROR: $error")
            }
        }
        return locationData
    }
}