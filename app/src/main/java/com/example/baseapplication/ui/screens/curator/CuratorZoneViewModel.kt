package com.example.baseapplication.ui.screens.curator

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.baseapplication.consts.PoICollection
import com.example.baseapplication.models.PointOfInterestModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class CuratorZoneViewModel : ViewModel() {
    private val TAG = "CuratorScreenViewModel"
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    var firestoreDocRegistration: ListenerRegistration? = null
    //private val PoICollection = "Points of Interest"

    var uiState = mutableStateOf(
        CuratorZoneUiState(
            firebaseAuth.currentUser,
            listOf()
        )
    )
        private set

    val authStateListener = FirebaseAuth.AuthStateListener {
        uiState.value = uiState.value.copy(user = it.currentUser)
        if (it.currentUser != null){
            Log.d(TAG, "authStateListener#firestoreDocRegistration uid: ${it.currentUser!!.uid}")

            firestoreDocRegistration = firestore.collection(PoICollection)
                .whereEqualTo("userid", it.currentUser!!.uid)
                .addSnapshotListener { value, e ->
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    if(value == null){
                        Log.d(TAG, "firestoreDocRegistration value is null")
                        return@addSnapshotListener
                    }


                    Log.d(TAG, "firestoreDocRegistration nr docs: ${value.documents.size}")



                    val poilist =
                        value.documents.mapNotNull { it.toObject(PointOfInterestModel::class.java) }

                    uiState.value=uiState.value.copy(points = poilist)
                }
        } else {
            firestoreDocRegistration?.remove()
        }
    }


    val _s = firebaseAuth.addAuthStateListener(authStateListener)



    fun onSignOutClick() {
        firebaseAuth.signOut()
    }

    fun deletePoIEntry(poi: PointOfInterestModel) {
        firestore.collection(PoICollection).document(poi.poi_uid).delete()
    }

    override fun onCleared() {
        super.onCleared()
        firebaseAuth.removeAuthStateListener(authStateListener)
        firestoreDocRegistration?.remove()
    }
}

data class CuratorZoneUiState(
    val user: FirebaseUser? = null,
    val points: List<PointOfInterestModel> = listOf(),
)


/*PointOfInterestModel(
                    name = "Refugio do Drinks",
                    userid = "uid",
                    type = "type",
                    address = "Rua de Calouste Gulbenkian, 3810-074 Aveiro",
                    imageUrl = "https://lh5.googleusercontent.com/p/AF1QipM53htCYlBapyhCJTubMWx-upwqoYv9SdyRnIu7=w408-h306-k-no",
                    longitude = -8.656334,
                    latitude = 40.636413
                )*/