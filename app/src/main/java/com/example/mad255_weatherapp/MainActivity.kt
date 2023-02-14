package com.example.mad255_weatherapp

import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mad255_weatherapp.databinding.ActivityMainBinding
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
    private lateinit var binding: ActivityMainBinding


    //response for the Api
    private lateinit var response: String

    //variables for latitude and longitude to determin user location
    var ZIP_CODE = "60051"
    var LAT = "42.24"
    var LON = "88.31"
    val API_KEY = "1db914c623ec2e5caba5ddfe8e12b0de"

    val API_KEY_LOCATION = "1622e930-ac9d-11ed-a940-ffdae417e345"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*start a new thread to run the API:
        * IO: network/api/web.
        * Main: anything working off main activity.
        * Default: for calculations.
        * */
        CoroutineScope(IO).launch {
            callWeatherAPI()
            //callLocationAPI()
        }

        binding.btnRefresh.setOnClickListener {
            if (binding.etZipCode.text.isNotEmpty()){
                CoroutineScope(IO).launch {
                    ZIP_CODE = binding.etZipCode.text.toString()
                    callLocationAPI()
                }
            }
        }
    }


    private suspend fun callLocationAPI(){
        val apiUrl = "https://app.zipcodebase.com/api/v1/search?apikey=${API_KEY_LOCATION}&codes=${ZIP_CODE}&country=us"
        val result = getResultFromAPI(apiUrl)
        Log.i("api_location", result.toString())
        if (result != null){
            try{
                updateLocationInfo(result)
            }catch (error: java.lang.Exception){
                Log.e("api error", "api error: ${error}")
            }finally {
                callWeatherAPI()
            }
        }
    }

    private suspend fun updateLocationInfo(incomingJSON: String){
        //get the JSON object from the api and extract data
        withContext(Main){
            val jsonObj = JSONObject(incomingJSON)
            val main = jsonObj.getJSONObject("results") //defined as a JsonObject
            val zipData = main.getJSONArray(ZIP_CODE) //this is defined as a JSONarray

            //intialize values.
            var zipCity = "nil"
            var zipState = "nil"
            for (i in 0 until zipData.length()){
                LON = zipData.getJSONObject(i).getString("longitude")
                LAT = zipData.getJSONObject(i).getString("latitude")
                zipState = zipData.getJSONObject(i).getString("state")
                zipCity = zipData.getJSONObject(i).getString("city")
            }
            binding.tvlocation.text = "Location: ${zipCity}, ${zipState}"
            //Log.i("api_location", LON)
            //binding.tvlocation.text="location: ${main.getString(ZIP_CODE)[1]}"
        }
    }


    private suspend fun callWeatherAPI(){
        val apiUrl = "https://api.openweathermap.org/data/2.5/weather?lat=${LAT}&lon=${LON}&units=imperial&appid=${API_KEY}"
        val result = getResultFromAPI(apiUrl)
        if (result != null) {
            updateWeatherInfo(result)
        }
    }
    private suspend fun getResultFromAPI(apiUrl:String): String? {
        try{
            //api url with api key and various settings
            response = URL(apiUrl).readText(Charsets.UTF_8)

        }catch (error: java.lang.Exception){
            // return the error if error happens
            Log.e("api error", "api error: ${error}")
            return null //halt if error to prevent app crash.
        }
        return response
    }
    private suspend fun updateWeatherInfo(incomingJSON: String){
        //get the JSON object from the api and extract data
        withContext(Main){
            val jsonObj = JSONObject(incomingJSON)
            val main = jsonObj.getJSONObject("main")
            binding.tvTemp.text= "${main.getString("temp")}°"
            binding.tvFeelsLike.text="${main.getString("feels_like")}°"
            binding.tvmaxTemp.text="max/min: ${main.getString("temp_max")}°" +
                    " - ${main.getString("temp_min")}°"
            binding.tvHumidity.text="humidity:${main.getString("humidity")}%"
            binding.tvConditions.text="pressure: ${main.getString("pressure")}"
            //binding.tvlocation.text="location: ${jsonObj.getString("name")}"
        }
    }
}