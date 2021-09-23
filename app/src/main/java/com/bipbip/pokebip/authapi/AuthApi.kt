package com.bipbip.pokebip.authapi

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("login/")
    fun postAuthLoginIn(
        @Body cred: ApiLoginCredentials
    ): Call<ApiAuthToken>

    @POST("signup/")
    fun postAuthSignIn(
        @Body cred: ApiSignInCredentials
    ): Call<ApiAuthToken>
}
