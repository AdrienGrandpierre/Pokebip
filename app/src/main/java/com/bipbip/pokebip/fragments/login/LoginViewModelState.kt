package com.bipbip.pokebip.fragments.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bipbip.pokebip.authapi.ApiAuthToken
import com.bipbip.pokebip.authapi.ApiLoginCredentials
import com.bipbip.pokebip.authapi.AuthApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private const val TAG = "LoginViewModel"

sealed class LoginViewModelState(
    open val token : String = "",
    open val errorMessage: String = "",
    open val loginButtonEnable: Boolean = false
) {
    object Loading : LoginViewModelState()
    data class Success(override val token: String) :
        LoginViewModelState(token = token)
    data class UpdateLogin(override val loginButtonEnable: Boolean) :
        LoginViewModelState(loginButtonEnable = loginButtonEnable)
    data class Failure(override val errorMessage: String) :
        LoginViewModelState(errorMessage = errorMessage, loginButtonEnable = true)
}

class LoginViewModel : ViewModel() {

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

    private val state = MutableLiveData<LoginViewModelState>()
    fun getState(): LiveData<LoginViewModelState> = state

    fun login(email: String, password: String) {
        state.value = LoginViewModelState.Loading

        val call = api.postAuthLoginIn(ApiLoginCredentials(email = email, password = password))

        call.enqueue(object : Callback<ApiAuthToken> {

            override fun onResponse(
                call: Call<ApiAuthToken>,
                response: Response<ApiAuthToken>
            ) {
                val res = response.body()
                if (res == null){
                    state.value = LoginViewModelState.Failure("Invalid credential")
                }else {
                    state.value = LoginViewModelState.Success(res.token)
                }
            }

            override fun onFailure(call: Call<ApiAuthToken>, t: Throwable) {
                Log.d(TAG, "onFailure: FAIL")
                state.value = LoginViewModelState.Failure("Error on datebase resquest")

            }

        })
    }

    fun UpdateLogin(email: String, password: String) {
        val buttonEnabled = email.isNotBlank() && password.isNotBlank()
        state.value = LoginViewModelState.UpdateLogin(buttonEnabled)
    }

}