package com.example.baseapplication.ui.screens.curator

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baseapplication.consts.PoICollection
import com.example.baseapplication.models.PointOfInterestModel
import com.example.baseapplication.ui.screens.auth.SignupUiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CuratorAddPoIViewModel: ViewModel() {
    private val TAG = "CuratorAddPoIViewModel"

    val firebaseAuth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()

    var uiState = mutableStateOf(CuratorAddPoIUiState())
        private set

    fun onNameChange(newValue: String) {
        uiState.value = uiState.value.copy(poi = uiState.value.poi.copy(name = newValue))
    }

    fun onTypeChange(newValue: String) {
        Log.d(TAG, "onTypeChange")
        uiState.value = uiState.value.copy(poi = uiState.value.poi.copy(type = newValue))
    }

    fun onImgUrlChange(newValue: Uri?) {
        Log.d(TAG, "onImgUrlChange")
        if(newValue==null){
            uiState.value=uiState.value.copy(errMsg = "No photo added!")
        }else {
            uiState.value = uiState.value.copy(localImageUri = newValue)
        }

    }

    fun onLocationChange(newLat: Double, newLng: Double, newAddress: String){
        Log.d(TAG, "onLocationChange")

        uiState.value = uiState.value.copy(poi = uiState.value.poi.copy(
            latitude = newLat,
            longitude = newLng,
            address = newAddress,
        ))
    }

    fun submitPoI(onSucess: ()-> Unit){
        Log.d(TAG, "submitPoI")

        var poi= uiState.value.poi
        uiState.value=uiState.value.copy(errMsg = "")

        if(poi.name.isEmpty()){
            uiState.value=uiState.value.copy(errMsg = "O nome nao esta preenchido!")
            return
        }

        if(uiState.value.localImageUri == null){
            uiState.value=uiState.value.copy(errMsg = "E necessario uma imagem!")
            return
        }

        if(poi.address.isEmpty()){
            uiState.value=uiState.value.copy(errMsg = "E necessario uma localizacao!")
            return
        }

        val imageRef = storage.reference.child("images/${uiState.value.localImageUri!!.lastPathSegment}")
        val uploadTask = imageRef.putFile(uiState.value.localImageUri!!)

        viewModelScope.launch {
            Log.d(TAG, "submitPoI - uploading image")
            val upload= uploadTask.await()
            Log.d(TAG, "submitPoI - upload: ${upload.error?.message ?: "success"}")

            Log.d(TAG, "submitPoI - getting image url")
            val imgUrl= imageRef.downloadUrl.await()

            Log.d(TAG, "submitPoI - writing to firestore")

            val doc = firestore.collection(PoICollection).add(poi.copy(imageUrl = imgUrl.toString(), userid = firebaseAuth.currentUser?.uid ?: "-1")).await()
            Log.d(TAG, "submitPoI - success")
            onSucess()


        }
    }

    fun isSubmitable():Boolean{
        val poi= uiState.value.poi
        return poi.name.isNotEmpty() && poi.type.isNotEmpty() && poi.address.isNotEmpty() && poi.imageUrl.isNotEmpty()
    }

}

data class CuratorAddPoIUiState(
    val errMsg: String = "",
    val localImageUri: Uri? =null,
    val poi: PointOfInterestModel = PointOfInterestModel(),

)