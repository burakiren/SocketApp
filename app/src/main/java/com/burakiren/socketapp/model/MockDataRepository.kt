package com.burakiren.socketapp.model

import android.util.Log
import com.burakiren.socketapp.data.ApiClient
import com.burakiren.socketapp.data.OperationCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MockDataRepository : MockDataSource {


    val TAG="MockDataRepository"

    private var call:Call<MockServiceResponse>?=null

    override fun retrieveDatas(callback: OperationCallback) {
        call= ApiClient.build()?.getDatas()
        call?.enqueue(object :Callback<MockServiceResponse>{
            override fun onFailure(call: Call<MockServiceResponse>, t: Throwable) {
                callback.onError(t.message)
            }

            override fun onResponse(call: Call<MockServiceResponse>, response: Response<MockServiceResponse>) {
                response?.body()?.let {
                    if(response.isSuccessful){
                        Log.v(TAG, "data ${it.data}")
                        callback.onSuccess(it.data)
                    }else{
                        callback.onError("error")
                    }
                }
            }
        })
    }

    override fun cancel() {
        call?.let {
            it.cancel()
        }
    }
}