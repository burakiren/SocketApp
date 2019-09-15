package com.burakiren.socketapp.view

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.burakiren.socketapp.R
import com.burakiren.socketapp.di.Injection
import com.burakiren.socketapp.model.MockData
import com.burakiren.socketapp.utils.Constants
import com.burakiren.socketapp.viewmodel.MainViewModel
import com.burakiren.socketapp.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_error.*
import okhttp3.OkHttpClient


class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: MockDataAdapter
    private lateinit var client: OkHttpClient

    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        client = OkHttpClient()
        setupViewModel()
        setupUI()

    }

    //ui
    private fun setupUI() {
        adapter = MockDataAdapter(viewModel.mockDatas.value ?: emptyList())
        rvData.layoutManager = LinearLayoutManager(this)
        rvData.adapter = adapter

        toolbar.title = Constants.CURRENT_USER
        etInput.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    viewModel.setData(etInput.text.toString()); true
                }
                else -> false
            }
        }

    }

    private fun setupViewModel() {
        viewModel =
            ViewModelProviders.of(this, ViewModelFactory(Injection.providerRepository())).get(MainViewModel::class.java)
        viewModel.mockDatas.observe(this, renderMuseums)
        viewModel.socketInit()

        viewModel.isViewLoading.observe(this, isViewLoadingObserver)
        viewModel.onMessageError.observe(this, onMessageErrorObserver)
        viewModel.isEmptyList.observe(this, emptyListObserver)
        viewModel.isSocketdata.observe(this, socketDataObserver)
    }

    //observers
    private val renderMuseums = Observer<List<MockData>> {
        Log.v(TAG, "data updated $it")
        layoutError.visibility = View.GONE
        layoutEmpty.visibility = View.GONE
        adapter.update(it)
    }

    private val isViewLoadingObserver = Observer<Boolean> {
        Log.v(TAG, "isViewLoading $it")
        val visibility = if (it) View.VISIBLE else View.GONE
        progressBar.visibility = visibility
    }

    private val onMessageErrorObserver = Observer<Any> {
        Log.v(TAG, "onMessageError $it")
        layoutError.visibility = View.VISIBLE
        layoutEmpty.visibility = View.GONE
        textViewError.text = "Error $it"
    }

    private val emptyListObserver = Observer<Boolean> {
        Log.v(TAG, "emptyListObserver $it")
        layoutEmpty.visibility = View.VISIBLE
        layoutError.visibility = View.GONE
    }


    private val socketDataObserver = Observer<String> {
        Log.v(TAG, "socketDataObserver $it")
        if (it.toString().contains("-")) {
            parseMockData(it.toString())
        } else {
            setMockStatus(it.toString())
        }

    }


    override fun onResume() {
        super.onResume()
        viewModel.loadDatas()
    }


    fun parseMockData(socketData: String) {
        if (viewModel.mockDatas.value?.size == 0) {
            return
        }
        var id: Int = 0
        try {
            id = socketData.split("-")[0].toInt()
        } catch (e: NumberFormatException) {
            // Invalid Data Format (string or sth)
        }
        var name = socketData.split("-")[1]
        viewModel.mockDatas.value?.forEach {
            if (id == it.id)
                it.name = name
        }
        adapter.update(viewModel.mockDatas.value!!)
    }

    fun setMockStatus(status : String){

        when(status) {
            Constants.LOGIN -> toolbar.setTitle(Constants.CURRENT_USER)
            Constants.LOGOUT -> toolbar.setTitle(Constants.LOGOUT)
            else -> println("Invalid Data")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopWebSocket()
    }

}
