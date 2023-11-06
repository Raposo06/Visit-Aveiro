package com.example.baseapplication.ui.screens.curator

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baseapplication.consts.PoICollection
import com.example.baseapplication.models.PointOfInterestModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CuratorEditViewModel: ViewModel() {
    private val TAG = "CuratorEditViewModel"

    val firestore = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()

    var uiState = mutableStateOf(CuratorEditPoIUiState())
        private set

    fun getPoI(docId: String, onSucess: ()->Unit){
        firestore.collection(PoICollection).document(docId).get().addOnSuccessListener {
            val newPoI = it.toObject(PointOfInterestModel::class.java)
            uiState.value= uiState.value.copy(poi = newPoI, localImageUri = Uri.parse(newPoI?.imageUrl?:""))
            onSucess()
        }
    }

    fun onNameChange(newValue: String) {
        uiState.value = uiState.value.copy(poi = uiState.value.poi!!.copy(name = newValue))
    }

    fun onTypeChange(newValue: String) {
        Log.d(TAG, "onTypeChange")
        uiState.value = uiState.value.copy(poi = uiState.value.poi!!.copy(type = newValue))
    }

    fun onImgUrlChange(newValue: Uri?) {
        Log.d(TAG, "onImgUrlChange")
        if(newValue==null){
            uiState.value=uiState.value.copy(errMsg = "No photo added!")
        }
        uiState.value = uiState.value.copy(localImageUri = newValue, changedPhoto = true)
    }

    fun onImgDelete() {
        Log.d(TAG, "onImgDelete")
        uiState.value = uiState.value.copy(localImageUri = null, changedPhoto = true)
    }

    fun onLocationChange(newLat: Double, newLng: Double, newAddress: String){
        Log.d(TAG, "onLocationChange")

        uiState.value = uiState.value.copy(poi = uiState.value.poi!!.copy(
            latitude = newLat,
            longitude = newLng,
            address = newAddress,
        ))
    }

    fun submitPoI(onSucess: ()-> Unit){
        Log.d(TAG, "submitPoI")

        var poi= uiState.value.poi!!
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

        viewModelScope.launch {
            if(uiState.value.changedPhoto){
                val imageRef = storage.reference.child("images/${uiState.value.localImageUri!!.lastPathSegment}")
                val uploadTask = imageRef.putFile(uiState.value.localImageUri!!)

                Log.d(TAG, "submitPoI - uploading image")
                val upload= uploadTask.await()
                Log.d(TAG, "submitPoI - upload: ${upload.error?.message ?: "success"}")

                Log.d(TAG, "submitPoI - getting image url")
                val imgUrl= imageRef.downloadUrl.await()

                poi= poi.copy(imageUrl = imgUrl.toString())
            }


            Log.d(TAG, "submitPoI - writing to firestore")

            firestore.collection(PoICollection).document(poi.poi_uid).set(poi).addOnCompleteListener {
                if(it.isComplete){
                    Log.d(TAG, "submitPoI - success")
                    onSucess()
                } else {
                    uiState.value=uiState.value.copy(errMsg = "Ocurreu um erro na atualizacao na base de dados!")
                }
            }
        }
    }
}

data class CuratorEditPoIUiState(
    val errMsg: String = "",
    val localImageUri: Uri? =null,
    val changedPhoto: Boolean =false,
    val poi: PointOfInterestModel? = null,
    )

fun curatorEditStateToAddState (uiState: CuratorEditPoIUiState): CuratorAddPoIUiState {
    return CuratorAddPoIUiState(poi = uiState.poi!!, localImageUri = uiState.localImageUri)
}