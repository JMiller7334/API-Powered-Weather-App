package com.example.mad255_weatherapp.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mad255_weatherapp.models.LocationApi
import com.example.mad255_weatherapp.models.LocationData
import com.example.mad255_weatherapp.models.WeatherApi
import com.example.mad255_weatherapp.models.WeatherData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class MainActivityViewModel: ViewModel() {

    private val weatherApi = WeatherApi()
    private val locationApi = LocationApi()

    private val _weatherLiveData = MutableLiveData<WeatherData?>()
    val weatherLiveData: LiveData<WeatherData?>
        get() = _weatherLiveData

    private val _locationLiveData = MutableLiveData<LocationData?>()
    val locationLiveData: LiveData<LocationData?>
        get() = _locationLiveData


    /*getWeather():
    * */
    fun getWeather(){
        CoroutineScope(IO).launch {
            val weatherResponse = weatherApi.callWeatherAPI(null, null)
            _weatherLiveData.postValue(weatherResponse)
        }
        Log.i("viewModel_debug", "app-viewModel: received:${weatherLiveData}")
    }

    /*GetWeatherByLocation():
    * */
    fun getWeatherByLocation(zipCode: String){
        CoroutineScope(IO).launch {
            val locationResponse = locationApi.callLocationAPI(zipCode)
            val weatherResponse = weatherApi.callWeatherAPI(locationResponse.latitude, locationResponse.longitude)
            _weatherLiveData.postValue(weatherResponse)
            _locationLiveData.postValue(locationResponse)
        }
        Log.i("_debug", "ViewModel: received on location call $locationLiveData")
    }
}