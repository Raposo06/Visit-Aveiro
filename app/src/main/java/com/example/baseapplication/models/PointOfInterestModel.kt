package com.example.baseapplication.models

data class PointOfInterestModel(
    val poi_uid:String="",
    val name:String="",
    val userid: String="",
    val type: String="",
    val address: String="",
    val imageUrl: String ="",
    val longitude: Double=0.0,
    val latitude: Double=0.0,
)
