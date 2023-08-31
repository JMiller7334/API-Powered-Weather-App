package com.example.mad255_weatherapp.models

/*LocationData Class
* holds data from a Json object that was received by an api call.
* */
data class LocationData (
    var longitude: String?,
    var latitude: String?,
    var state: String?,
    var city: String?
)