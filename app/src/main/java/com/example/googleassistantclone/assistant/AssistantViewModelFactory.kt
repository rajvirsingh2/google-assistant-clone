package com.example.googleassistantclone.assistant

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.googleassistantclone.data.AssistantDao

@Suppress("UNCHECKED_CAST")
class AssistantViewModelFactory(private val dataSource: AssistantDao,
                                private val application: Application): ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(AssistantViewModel::class.java)){
            return AssistantViewModel(dataSource, application) as T
        }
        throw IllegalAccessException("Unknown ViewModel Class")
    }
}