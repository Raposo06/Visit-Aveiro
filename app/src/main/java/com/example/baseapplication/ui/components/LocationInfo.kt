package com.example.baseapplication.ui.components

import androidx.compose.runtime.MutableState

data class LocationInfo(
    var address: String = "",
    var imageUrl: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var name: String = ""
)
