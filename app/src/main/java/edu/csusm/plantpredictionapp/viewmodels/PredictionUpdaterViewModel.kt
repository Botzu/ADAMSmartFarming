package edu.csusm.plantpredictionapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// sending a message to displayPrediction to trigger update
class PredictionUpdaterViewModel: ViewModel() {
    val message = MutableLiveData<String>()
    fun sendMessage(text: String) {
        message.value = text
    }
}