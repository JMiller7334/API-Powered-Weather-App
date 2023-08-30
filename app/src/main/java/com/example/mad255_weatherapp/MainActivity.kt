package com.example.mad255_weatherapp
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.mad255_weatherapp.databinding.ActivityMainBinding
import com.example.mad255_weatherapp.models.LocationData
import com.example.mad255_weatherapp.models.WeatherData
import com.example.mad255_weatherapp.viewModels.MainActivityViewModel

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

    private fun updateWeatherUI(weatherData: WeatherData){
        Log.i("view_debug", "appView: updating weather UI")
        val stringTemp = getString(R.string.string_temp)
        val stringFeelsLike = getString(R.string.string_feels_like)
        val stringMaxMin = getString(R.string.string_max_min)
        val stringHumidity = getString(R.string.string_humidity)
        val stringPressure = getString(R.string.string_pressure)

        binding.tvTemp.text = String.format(stringTemp, weatherData.temp)
        binding.tvFeelsLike.text = String.format(stringFeelsLike, weatherData.feelsLike)
        binding.tvmaxTemp.text = String.format(stringMaxMin, weatherData.minTemp, weatherData.maxTemp)
        binding.tvHumidity.text = String.format(stringHumidity, weatherData.humidity)
        binding.tvPressure.text = String.format(stringPressure, weatherData.pressure)
    }

    private fun updateLocationUI(locationData: LocationData){
        Log.i("view_debug", "appView: updating location UI")
        if (locationData.longitude != null && locationData.latitude != null) {
            val stringLocation = getString(R.string.string_location)
            binding.tvlocation.text = String.format(stringLocation, locationData.city, locationData.state)
        } else {
            Toast.makeText(this, "Error occurred while retrieving weather", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //configure the viewModel
        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]

        //update the UI when Weather api updates live data
        viewModel.weatherLiveData.observe(this) { weatherData ->
            updateWeatherUI(weatherData)
        }

        //update the UI when location api updates live data
        viewModel.locationLiveData.observe(this) { locationData ->
            updateLocationUI(locationData)
        }

        //get weather on start up
        viewModel.getWeatherByLocation("52240")
        //listener for the button
        binding.btnRefresh.setOnClickListener {
            if (binding.etZipCode.text.isNotEmpty()) {
                viewModel.getWeatherByLocation(binding.etZipCode.text.toString())
            }
        }
    }
}