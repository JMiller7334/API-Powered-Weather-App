package com.example.mad255_weatherapp.models

/*LocationData Class
* holds data from a Json object that was received by an api call.
* converts weather data to from fahrenheit to celsius and back again.
* */
data class WeatherData(
    var isCelsius: Boolean = false,
    var temp: String,
    var feelsLike: String,
    var maxTemp: String,
    var minTemp: String,
    val humidity: String,
    val pressure: String
)  {
    private fun fahrenheitToCelsius(fahrenheit: String): String {
        val fahrenheitValue = fahrenheit.toDoubleOrNull()
        if (fahrenheitValue != null) {
            val celsiusValue = (fahrenheitValue - 32) * 5 / 9
            return String.format("%.2f", celsiusValue)
        }
        return "-" //emtpy value if there was nothing to convert.
    }

    private fun celsiusToFahrenheit(celsius: String): String {
        val celsiusValue = celsius.toDoubleOrNull()
        if (celsiusValue != null) {
            val fahrenheitValue = celsiusValue * 9 / 5 + 32
            return String.format("%.2f", fahrenheitValue)
        }
        return "-" // empty value if there was nothing to convert.
    }

    fun convert(toCelsius: Boolean) {
        this.isCelsius = toCelsius
        if (toCelsius) {
            this.temp = fahrenheitToCelsius(this.temp)
            this.feelsLike = fahrenheitToCelsius(this.feelsLike)
            this.maxTemp = fahrenheitToCelsius(this.maxTemp)
            this.minTemp = fahrenheitToCelsius(this.minTemp)
        } else {
            this.temp = celsiusToFahrenheit(this.temp)
            this.feelsLike = celsiusToFahrenheit(this.feelsLike)
            this.maxTemp = celsiusToFahrenheit(this.maxTemp)
            this.minTemp = celsiusToFahrenheit(this.minTemp)
        }
    }
}