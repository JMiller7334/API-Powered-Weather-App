package com.example.mad255_weatherapp
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
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

/*Note: internet use permissions are applied in the manifest.*/


/*VIEW:
* this handles updating the UI for the app and it also
* notifies the user if it thinks they have entered an invalid
* zip code.
* */
class MainActivity : AppCompatActivity() {
    //setup var for binding(synthetic replacement)
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var binding: ActivityMainBinding

    /*function update UI:
    * receives a WeatherData Object/class as parameter - containing data received from the
    * weather api and updates the user interface with this data.*/
    private fun updateWeatherUI(weatherData: WeatherData){
        Log.i("view_debug", "appView: updating weather UI")
        val stringTemp = getString(R.string.string_temp)
        val stringFeelsLike = getString(R.string.string_feels_like)
        val stringMaxMin = getString(R.string.string_max_min)
        val stringHumidity = getString(R.string.string_humidity)
        val stringPressure = getString(R.string.string_pressure)

        var tempFormat = "F"
        if (weatherData.isCelsius){
            tempFormat = "C"
        }

        binding.tvTemp.text = String.format(stringTemp, weatherData.temp, tempFormat)
        binding.tvFeelsLike.text = String.format(stringFeelsLike, weatherData.feelsLike, tempFormat)
        binding.tvMaxTemp.text = String.format(stringMaxMin, weatherData.minTemp, tempFormat , weatherData.maxTemp, tempFormat)
        binding.tvHumidity.text = String.format(stringHumidity, weatherData.humidity)
        binding.tvPressure.text = String.format(stringPressure, weatherData.pressure)
    }

    /*function updateLocationUI
    * Receives locationData object/class that contains data received by the
    * location api then updates the user UI with that data.
     */
    private fun updateLocationUI(locationData: LocationData){
        Log.i("view_debug", "appView: updating location UI")
        if (locationData.longitude != null && locationData.latitude != null) {
            val stringLocation = getString(R.string.string_location)
            binding.tvLocation.text = String.format(stringLocation, locationData.city, locationData.state)
        } else {
            Toast.makeText(this, "Please ensure zip code is correct.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //configure the viewModel
        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]

        //observe live data: calls update function when data changes.
        viewModel.weatherLiveData.observe(this) { weatherData ->
            updateWeatherUI(weatherData)
        }
        viewModel.locationLiveData.observe(this) { locationData ->
            updateLocationUI(locationData)
        }

        //get weather on start up using a default zip code
        viewModel.getWeatherByLocation("52240")

        //spinner config:
        val spinnerItems = listOf("°F", "°C")
        val spinnerAdapter = ArrayAdapter(this, R.layout.spinner_item, spinnerItems)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        binding.spinnerOptions.adapter = spinnerAdapter

        //calls viewModel to have existing data converted.
        binding.spinnerOptions.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Log.i("view_debug", "user spinner input: $position")
                viewModel.selectedFormat = position
                viewModel.updateFormat()
                binding.spinnerOptions.setSelection(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //does nothing
            }
        }
        binding.spinnerOptions.setSelection(0)

        //listener for the button
        binding.btnRefresh.setOnClickListener {
            if (binding.etZipCode.text.isNotEmpty()) {
                viewModel.getWeatherByLocation(binding.etZipCode.text.toString())
            } else {
                Toast.makeText(this, "Please enter a zip code.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}