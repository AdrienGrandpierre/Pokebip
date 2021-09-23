package com.bipbip.pokebip.authapi

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiAuthToken(
    val token: String,
)

@JsonClass(generateAdapter = true)
data class ApiLoginCredentials(
    val email: String,
    val password: String
)

@JsonClass(generateAdapter = true)
data class ApiSignInCredentials(
    val username: String,
    val email: String,
    val password: String
)