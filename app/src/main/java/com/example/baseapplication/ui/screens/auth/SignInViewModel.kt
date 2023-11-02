package com.example.baseapplication.ui.screens.auth

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baseapplication.ext.isValidEmail

import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException

import kotlinx.coroutines.launch

import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException


class SignInViewModel : ViewModel() {
    private val firebaseAuth = FirebaseAuth.getInstance()
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

    fun onSignInClick(onSucess: () -> Unit) {
        if (!email.isValidEmail()) {
            Log.e(TAG, "onSignInClick: invalid email")
            uiState.value = uiState.value.copy(errMsg = "Email nao e valido")
            return
        }

        if (password.isBlank()) {
            Log.e(TAG, "onSignInClick: empty password")
            uiState.value = uiState.value.copy(errMsg = "password nao pode ser vazia")
            return
        }

        viewModelScope.launch {
            var t = firebaseAuth.signInWithEmailAndPassword(email, password)

            t.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    uiState.value = uiState.value.copy(errMsg = "")
                    onSucess()
                } else {
                    Log.i(TAG, "onClick: ", task.exception)

                    try {
                        throw task.exception!!
                    } catch (e: FirebaseNetworkException) {
                        uiState.value = uiState.value.copy(errMsg = "Servidor indisponivel")
                    } catch (e: FirebaseAuthInvalidUserException) {
                        uiState.value = uiState.value.copy(errMsg = "Conta nao existente")
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        uiState.value = uiState.value.copy(errMsg = "Senha incorreta")
                    } catch (e: FirebaseException) {
                        uiState.value = uiState.value.copy(errMsg = "Crendencias incorretas")
                    } catch (e: Exception) {
                        //uiState.value = uiState.value.copy(errMsg = (e is FirebaseAuthException).toString())
                        Log.e(TAG, "onClick: ${e.message}")
                    }

                }
            }
        }

    }

    fun onForgotPasswordClick() {
        if (!email.isValidEmail()) {
            Log.e(TAG, "onForgotPasswordClick: invalid email")
            uiState.value = uiState.value.copy(errMsg = "invalid email")
            return
        }

        viewModelScope.launch {
            firebaseAuth.sendPasswordResetEmail(email)
            Log.e(TAG, "onForgotPasswordClick: reset mail sent")
            uiState.value = uiState.value.copy(errMsg = "reset mail sent")
        }
    }

}

data class SiginUiState(
    val email: String = "",
    val password: String = "",
    val errMsg: String = "",
)