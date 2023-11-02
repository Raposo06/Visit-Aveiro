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
import kotlinx.coroutines.launch


class SignUpViewModel : ViewModel() {
    private val firebaseAuth = Firebase.auth
    private val TAG = "SignInViewModel"

    var uiState = mutableStateOf(SiginUiState())
        private set

    private val email
        get() = uiState.value.email
    private val password
        get() = uiState.value.password

    fun onEmailChange(newValue: String) {
        uiState.value = uiState.value.copy(email = newValue)
    }

    fun onPasswordChange(newValue: String) {
        uiState.value = uiState.value.copy(password = newValue)
    }

    fun onSignUpClick(onSucess: () -> Unit) {
        if (!email.isValidEmail()) {
            Log.e(TAG, "onSignInClick: invalid email")
            uiState.value = uiState.value.copy(errMsg = "invalid email")
            return
        }

        if (password.isBlank()) {
            Log.e(TAG, "onSignInClick: empty password")
            uiState.value = uiState.value.copy(errMsg = "empty password")
            return
        }

        viewModelScope.launch {
            var t = firebaseAuth.createUserWithEmailAndPassword(email, password)

            t.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    uiState.value = uiState.value.copy(errMsg = "")
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
    val email: String = "",
    val password: String = "",
    val errMsg: String = "",
)