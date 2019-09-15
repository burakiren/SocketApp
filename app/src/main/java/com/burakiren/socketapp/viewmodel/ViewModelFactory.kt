package com.burakiren.socketapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.burakiren.socketapp.model.MockDataSource

class ViewModelFactory(private val repository:MockDataSource): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel(repository) as T
    }
}