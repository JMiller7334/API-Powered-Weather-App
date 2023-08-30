package com.example.mad255_weatherapp

import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.mad255_weatherapp.databinding.ActivityMainBinding
import com.example.mad255_weatherapp.models.LocationData
import com.example.mad255_weatherapp.models.WeatherData
import com.example.mad255_weatherapp.viewModels.MainActivityViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.nio.charset.Charset

//Code by Jacob Miller 2023 - MAD255 - ANDROID 3
/*APIs used
* OpenWeatherMap: weather API
* ZipCodeBase - Zipcode API
* */

/*internet use permissions are applied in the manifest.

* */

class MainActivity : AppCompatActivity() {
    //setup var for binding(synthetic replacement)
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var binding: ActivityMainBinding

    private fun updateWeatherUI(weatherData: WeatherData?){
        Log.i("view_debug", "appView: updating weather UI")
        if (weatherData != null) {
            binding.tvTemp.text = "${weatherData.temp}°"
            binding.tvFeelsLike.text = "${weatherData.feelsLike}°"
            binding.tvmaxTemp.text = "max/min: ${weatherData.maxTemp}°" +
                    " - ${weatherData?.minTemp}°"
            binding.tvHumidity.text = "humidity:${weatherData.humidity}%"
            binding.tvConditions.text = "pressure: ${weatherData.pressure}"
        } else {
            Toast.makeText(this, "Error occured getting weather data.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateLocationUI(locationData: LocationData?){
        Log.i("view_debug", "appView: updating location UI")
        if (locationData != null) {
            val strgCity = locationData.city
            val strgState = locationData.state
            var strgLabel = "Unknown, Unknown"

            if (strgCity != null && strgState != null) {
                strgLabel = "${strgCity}, $strgState"
            }
            binding.tvlocation.text = strgLabel
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //configure the viewModel
        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]

        //updtes the UI when Weather api updates live data
        viewModel.weatherLiveData.observe(this) { weatherData ->
            updateWeatherUI(weatherData)
        }

        //update the UI when location api updates live data
        viewModel.locationLiveData.observe(this) { locationData ->
            updateLocationUI(locationData)
        }

        //get weather on start up
        viewModel.getWeather()
        //listener for the button
        binding.btnRefresh.setOnClickListener {
            if (binding.etZipCode.text.isNotEmpty()) {
                viewModel.getWeatherByLocation(binding.etZipCode.text.toString())
            }
        }
    }
}