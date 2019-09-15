package com.burakiren.socketapp.data

import com.burakiren.socketapp.model.MockData
import com.burakiren.socketapp.model.MockServiceResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET

interface RetrofitService {
    @GET("mock-api/db")
    fun getDatas(): Call<MockServiceResponse>
}