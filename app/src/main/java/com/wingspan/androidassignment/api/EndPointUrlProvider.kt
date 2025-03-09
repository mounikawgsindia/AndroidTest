package com.wingspan.androidassignment.api



import com.wingspan.androidassignment.model.AccountsResponseModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface EndPointUrlProvider {


    //Viedos
    @GET("Fillaccounts/nadc/2024-2025")
    suspend fun getAccount():  Response<String>

}