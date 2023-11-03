package com.example.baseapplication.ui.screens.auth

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baseapplication.ext.isValidEmail
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.launch


class SignUpViewModel : ViewModel() {
    private val firebaseAuth = Firebase.auth
    private val TAG = "SignInViewModel"

    var uiState = mutableStateOf(SignupUiState())
        private set

    private val name
        get() = uiState.value.name
    private val email
        get() = uiState.value.email
    private val password
        get() = uiState.value.password


    fun onNameChange(newValue: String) {
        uiState.value = uiState.value.copy(name = newValue)
    }
    fun onEmailChange(newValue: String) {
        uiState.value = uiState.value.copy(email = newValue)
    }

    fun onPasswordChange(newValue: String) {
        uiState.value = uiState.value.copy(password = newValue)
    }

    fun onSignUpClick(onSucess: () -> Unit) {
        if (name.isBlank()) {
            Log.e(TAG, "onSignInClick: Empty name")
            uiState.value = uiState.value.copy(errMsg = "Empty name")
            return
        }

        if (!email.isValidEmail()) {
            Log.e(TAG, "onSignInClick: Invalid email")
            uiState.value = uiState.value.copy(errMsg = "Invalid email")
            return
        }

        if (password.isBlank()) {
            Log.e(TAG, "onSignInClick: Empty password")
            uiState.value = uiState.value.copy(errMsg = "Empty password")
            return
        }

        viewModelScope.launch {
            var t = firebaseAuth.createUserWithEmailAndPassword(email, password)

            t.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    uiState.value = uiState.value.copy(errMsg = "")
                    firebaseAuth.currentUser?.updateProfile(userProfileChangeRequest {
                        displayName= name
                    }
                    )?.addOnCompleteListener { if (task.isSuccessful){
                        Log.i(TAG, "onSignUpClick: Profile update with success")
                    }
                    }
                    onSucess()
                } else {
                    Log.i(TAG, "onSignUpClick: ", task.exception)

                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthWeakPasswordException) {
                        uiState.value = uiState.value.copy(errMsg = "Senha fraca!\ntem de ter mais de 6 carateres!")

                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        uiState.value = uiState.value.copy(errMsg = "Credenciais Invalidas")
                    } catch (e: FirebaseAuthUserCollisionException) {
                        uiState.value = uiState.value.copy(errMsg = "Utilizador existente")
                    } catch (e: Exception) {
                        Log.e(TAG, "onSignUpClick: ${e.message}")
                    }
                }
            }
        }

    }


}

data class SignupUiState(
    val name:String ="",
    val email: String = "",
    val password: String = "",
    val errMsg: String = "",
)