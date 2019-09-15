package com.burakiren.socketapp.data

interface OperationCallback {
    fun onSuccess(obj:Any?)
    fun onError(obj:Any?)
}