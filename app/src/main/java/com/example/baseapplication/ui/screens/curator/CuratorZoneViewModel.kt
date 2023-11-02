package com.example.baseapplication.ui.screens.curator

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.baseapplication.models.PointOfInterestModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class CuratorZoneViewModel : ViewModel() {
    private val TAG = "CuratorScreenViewModel"
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val user = firebaseAuth.currentUser

    var uiState = mutableStateOf(
        CuratorZoneUiState(
            firebaseAuth.currentUser,
            listOf(
                PointOfInterestModel(
                    name = "Refugio do Drinks",
                    userid = "uid",
                    type = "type",
                    address = "Rua de Calouste Gulbenkian, 3810-074 Aveiro",
                    imageUrl = "https://lh5.googleusercontent.com/p/AF1QipM53htCYlBapyhCJTubMWx-upwqoYv9SdyRnIu7=w408-h306-k-no",
                    longitude = -8.656334,
                    latitude = 40.636413
                )
            )
        )
    )
        private set

    val authStateListener = FirebaseAuth.AuthStateListener {
        uiState.value = uiState.value.copy(user = it.currentUser)
    }


    val _s = firebaseAuth.addAuthStateListener(authStateListener)

    fun onSignOutClick() {
        firebaseAuth.signOut()
    }

    //todo
    fun updatePoIEntry(poi: PointOfInterestModel) {
        Log.d(TAG, "on update click")
    }

    //todo
    fun deletePoIEntry(poi: PointOfInterestModel) {
        Log.d(TAG, "on delete click")
    }

    override fun onCleared() {
        super.onCleared()
        firebaseAuth.removeAuthStateListener(authStateListener)
    }
}

data class CuratorZoneUiState(
    val user: FirebaseUser? = null,
    val points: List<PointOfInterestModel> = listOf(),
)