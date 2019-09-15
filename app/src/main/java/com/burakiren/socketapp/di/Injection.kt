package com.burakiren.socketapp.di

import com.burakiren.socketapp.model.MockDataRepository
import com.burakiren.socketapp.model.MockDataSource

object Injection {

    fun providerRepository():MockDataSource{
        return MockDataRepository()
    }
}