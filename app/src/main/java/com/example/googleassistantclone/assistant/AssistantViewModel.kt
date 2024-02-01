package com.example.googleassistantclone.assistant

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.googleassistantclone.data.AssistantDao
import com.example.googleassistantclone.data.Assistant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AssistantViewModel(private val database: AssistantDao,
                         application: Application): AndroidViewModel(application)
{

    private var viewModelJob = Job()

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var currentMessage   = MutableLiveData<Assistant?>()

    val message = database.getAllMessages()

    init {
        initializeCurrentMessage()
    }

    private fun initializeCurrentMessage() {
        uiScope.launch {
            currentMessage.value = getCurrentMessageFromDatabase()
        }
    }

    private suspend fun getCurrentMessageFromDatabase(): Assistant? {
        return withContext(Dispatchers.IO){
            var message = database.getCurrentMessage()
            if(message?.assistantMessage == "DEFAULT_MESSAGE" || message?.humanMessage == "DEFAULT_MESSAGE"){
                message = null
            }
            message
        }
    }

    private suspend fun insert(message: Assistant){
        withContext(Dispatchers.IO){
            database.insert(message)
        }
    }

    fun onClear(){
        uiScope.launch {
            clear()
            currentMessage.value = null
        }
    }

    private suspend fun clear(){
        withContext(Dispatchers.IO){
            database.clear()
        }
    }

    fun sendMessageToDatabase(assistantMessage : String, humanMessage : String){
        uiScope.launch{
            val newAssistant = Assistant()
            newAssistant.assistantMessage = assistantMessage
            newAssistant.humanMessage = humanMessage
            insert(newAssistant)
            currentMessage.value = getCurrentMessageFromDatabase()
        }
    }

}