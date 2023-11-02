package com.example.baseapplication.ui.screens.curator

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class CuratorZoneViewModel: ViewModel() {
    private val TAG = "CuratorScreenViewModel"
    private val fauth = FirebaseAuth.getInstance()
    private val user = fauth.currentUser

    var uiState = mutableStateOf(CuratorZoneUiState(user))
        private set

    val authStateListener = FirebaseAuth.AuthStateListener {
        uiState.value=uiState.value.copy(user=it.currentUser)
    }

    val s =fauth.addAuthStateListener(authStateListener)

    fun onSignOutClick(){
        fauth.signOut()
    }

    override fun onCleared() {
        super.onCleared()
        fauth.removeAuthStateListener(authStateListener)
    }


}

data class CuratorZoneUiState(
    val user: FirebaseUser? = null,
)