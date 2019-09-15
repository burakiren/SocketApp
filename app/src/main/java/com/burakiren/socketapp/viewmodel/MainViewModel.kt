package com.burakiren.socketapp.viewmodel

import android.content.BroadcastReceiver
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.burakiren.socketapp.data.OperationCallback
import com.burakiren.socketapp.model.MockData
import com.burakiren.socketapp.model.MockDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*

class MainViewModel(private val repository: MockDataSource): ViewModel() {

    private lateinit var webSocket: WebSocket

    private val _mockDatas = MutableLiveData<List<MockData>>().apply { value = emptyList() }
    val mockDatas: LiveData<List<MockData>> = _mockDatas

    private val _isViewLoading=MutableLiveData<Boolean>()
    val isViewLoading:LiveData<Boolean> = _isViewLoading

    private val _onMessageError=MutableLiveData<Any>()
    val onMessageError:LiveData<Any> = _onMessageError

    private val _isEmptyList=MutableLiveData<Boolean>()
    val isEmptyList:LiveData<Boolean> = _isEmptyList

    private val _isSocketData=MutableLiveData<String>()
    val isSocketdata:LiveData<String> = _isSocketData

    fun loadDatas(){
        _isViewLoading.postValue(true)
        repository.retrieveDatas(object: OperationCallback {
            override fun onError(obj: Any?) {
                _isViewLoading.postValue(false)
                _onMessageError.postValue( obj)
            }

            override fun onSuccess(obj: Any?) {
                _isViewLoading.postValue(false)

                if(obj!=null && obj is List<*>){
                    if(obj.isEmpty()){
                        _isEmptyList.postValue(true)
                    }else{
                        _mockDatas.value= obj as List<MockData>
                    }
                }
            }
        })
    }

    fun socketInit(){
        var client = OkHttpClient()
        var request = Request.Builder().url("ws://echo.websocket.org").build()
        var listener = MockWebSocketListener()
        webSocket = client.newWebSocket(request, listener)
        client.dispatcher().executorService().shutdown()

    }

    fun stopWebSocket(){
        if (webSocket != null) {
            webSocket!!.close(1000, "disconnect")
        }
    }

    fun setData(inputData : String){
        GlobalScope.launch {
            withContext(Dispatchers.Main){ _isSocketData.value = inputData }
        }
    }

    inner class MockWebSocketListener : WebSocketListener() {

        private val NORMAL_CLOSURE_STATUS = 1000
        private lateinit var viewModel: MainViewModel
        private lateinit var myReceiver: BroadcastReceiver



        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket, response)
            Log.i("WebSocketListener", "onOpen")

        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)
            Log.i("WebSocketListener", "onFailure $response")
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosing(webSocket, code, reason)
            webSocket.close(NORMAL_CLOSURE_STATUS, null)
            Log.i("WebSocketListener", "onClosing")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            super.onMessage(webSocket, text)
            Log.i("WebSocketListener", "onMessage ($text)")
            GlobalScope.launch {
                withContext(Dispatchers.Main){ _isSocketData.value = text }
            }

        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosed(webSocket, code, reason)
            Log.i("WebSocketListener", "onClosed")
        }


    }

}