package com.example.baseapplication.models

import com.google.firebase.firestore.DocumentId

data class PointOfInterestModel(
    @DocumentId
    val poi_uid:String="",
    val userid: String="",

    val name:String="",
    val type: String=PointOfInterestTypeEnum.values()[0].name,

    val imageUrl: String ="",

    val address: String="",
    val longitude: Double=0.0,
    val latitude: Double=0.0,
)

enum class PointOfInterestTypeEnum {
    Lazer,
    Historia,
    Gastronomia,
}