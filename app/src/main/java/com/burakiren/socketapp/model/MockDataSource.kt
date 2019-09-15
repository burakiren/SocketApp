package com.burakiren.socketapp.model

import com.burakiren.socketapp.data.OperationCallback

interface MockDataSource {
    fun retrieveDatas(callback: OperationCallback)
    fun cancel()
}