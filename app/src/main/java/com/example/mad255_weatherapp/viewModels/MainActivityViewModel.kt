package com.example.mad255_weatherapp.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mad255_weatherapp.api.LocationApi
import com.example.mad255_weatherapp.models.LocationData
import com.example.mad255_weatherapp.api.WeatherApi
import com.example.mad255_weatherapp.models.WeatherData
import com.example.mad255_weatherapp.repositories.LocationRepository
import com.example.mad255_weatherapp.repositories.WeatherRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class MainActivityViewModel: ViewModel() {

    private val weatherApi = WeatherApi()
    private val locationApi = LocationApi()

    private val weatherRepository = WeatherRepository(weatherApi)
    private val locationRepository = LocationRepository(locationApi)

    private val _weatherLiveData = MutableLiveData<WeatherData>()
    val weatherLiveData: LiveData<WeatherData>
        get() = _weatherLiveData

    private val _locationLiveData = MutableLiveData<LocationData>()
    val locationLiveData: LiveData<LocationData>
        get() = _locationLiveData

    /*GetWeatherByLocation():
    * */
    fun getWeatherByLocation(zipCode: String){
        CoroutineScope(IO).launch {
            val locationResponse = locationRepository.getLocationData(zipCode)
            _locationLiveData.postValue(locationResponse)

            if (locationResponse.latitude != null && locationResponse.longitude != null) {
                val weatherResponse = weatherRepository.getWeatherData(
                    locationResponse.latitude,
                    locationResponse.longitude)

                //only update the live data if the api call was successful.
                if (weatherResponse != null) {
                    _weatherLiveData.postValue(weatherResponse)
                }
            }
        }
        Log.i("_debug", "ViewModel: received on location call $locationLiveData")
    }
}