package com.example.baseapplication.ui.components

data class LocationInfo(
    var address: String = "",
    var imageUrl: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var name: String = "",
    var localType: String = "",
    var user_ID: String = ""
)
