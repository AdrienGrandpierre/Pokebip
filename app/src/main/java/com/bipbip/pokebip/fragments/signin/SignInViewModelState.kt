package com.bipbip.pokebip.fragments.signin

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bipbip.pokebip.authapi.ApiAuthToken
import com.bipbip.pokebip.authapi.ApiLoginCredentials
import com.bipbip.pokebip.authapi.ApiSignInCredentials
import com.bipbip.pokebip.authapi.AuthApi
import com.bipbip.pokebip.fragments.login.LoginViewModelState
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private const val TAG = "SignInViewModelState"

sealed class SignInViewModelState(
    open val token : String = "",
    open val errorMessage: String = "",
    open val signInButtonEnable: Boolean = false
) {
    object Loading : SignInViewModelState()
    data class Success(override val token: String) :
        SignInViewModelState(token = token)
    data class UpdateSignIn(override val signInButtonEnable: Boolean) :
        SignInViewModelState(signInButtonEnable = signInButtonEnable)
    data class Failure(override val errorMessage: String) :
        SignInViewModelState(errorMessage = errorMessage, signInButtonEnable = true)
}

class SignInViewModel : ViewModel() {

    private val api: AuthApi

    init {
        // WARNING
        // This init should be done ONCE in the app
        // Application class is a good place
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://auth.pokedex.com/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

        api = retrofit.create(AuthApi::class.java)
    }

    private val state = MutableLiveData<SignInViewModelState>()
    fun getState(): LiveData<SignInViewModelState> = state

    fun signin(username: String,email: String, password: String) {
        state.value = SignInViewModelState.Loading

        val call = api.postAuthSignIn(ApiSignInCredentials(
            username = username,
            email = email,
            password = password
        ))

        call.enqueue(object : Callback<ApiAuthToken> {

            override fun onResponse(
                call: Call<ApiAuthToken>,
                response: Response<ApiAuthToken>
            ) {
                val res = response.body()
                if (res == null){
                    state.value = SignInViewModelState.Failure("Invalid credential")
                }else {
                    state.value = SignInViewModelState.Success(res.token)
                }
            }

            override fun onFailure(call: Call<ApiAuthToken>, t: Throwable) {
                Log.d(TAG, "onFailure: FAIL")
                state.value = SignInViewModelState.Failure("Error on datebase resquest")
            }

        })
    }

    fun UpdateSignIn(username: String, email: String, password: String) {
        val buttonEnabled = username.isNotBlank() && email.isNotBlank() && password.isNotBlank()
        state.value = SignInViewModelState.UpdateSignIn(buttonEnabled)
    }
}