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

/*ViewModel:
* Live Data is initialized here
* Repositories are called from here
* */
class MainActivityViewModel: ViewModel() {
    //init api:
    private val weatherApi = WeatherApi()
    private val locationApi = LocationApi()
    //init live data:
    private val weatherRepository = WeatherRepository(weatherApi)
    private val locationRepository = LocationRepository(locationApi)

    private val _weatherLiveData = MutableLiveData<WeatherData>()
    val weatherLiveData: LiveData<WeatherData>
        get() = _weatherLiveData

    private val _locationLiveData = MutableLiveData<LocationData>()
    val locationLiveData: LiveData<LocationData>
        get() = _locationLiveData

    /* selected temperature format:
    0 = Fahrenheit, 1 = Celsius
     */
    var selectedFormat = 0
    var lastResponse: WeatherData? = null
    /*updateFormat():
    * handles converting the format of the temperature when the user
    * changes it via UI spinner.
    * */
    fun updateFormat(){
        if (lastResponse != null){
            if (selectedFormat == 0) {
                lastResponse!!.convert(false)
            } else {
                lastResponse!!.convert(true)
            }
            Log.i("viewModel_debug", "posted live data for weatherData")
            _weatherLiveData.postValue(lastResponse)
        }
    }


    /*GetWeatherByLocation():
    * Coroutines are handled in here:
    function first calls the LocationRepository; location info is received here.

    * Location Live Data will always be updated even if null is returned.
    * View checks for null so that it can notify the user.

    * Weather Live Data is only updated if the function/api succeeds.
    * */
    fun getWeatherByLocation(zipCode: String){
        CoroutineScope(IO).launch {
            val locationResponse = locationRepository.getLocationData(zipCode)
            _locationLiveData.postValue(locationResponse)

            val weatherResponse = weatherRepository.getWeatherData(
                locationResponse.latitude,
                locationResponse.longitude)

            //only update the live data if the api call was successful.
            if (weatherResponse != null) {
                if (selectedFormat == 1){
                    weatherResponse.convert(true)
                    Log.i("viewModel_debug", "converting weatherData to celsius.")
                }
                lastResponse = weatherResponse
                _weatherLiveData.postValue(weatherResponse)
            }
        }
        Log.i("_debug", "ViewModel: received on location call $locationLiveData")
    }
}