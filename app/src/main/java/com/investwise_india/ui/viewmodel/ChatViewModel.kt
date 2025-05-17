package com.investwise_india.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.investwise_india.chatbot.MessageModel
import com.investwise_india.chatbot.generateGeminiPrompt
import com.investwise_india.chatbot.mutualFundCategory
import com.investwise_india.chatbot.FundMeta
import com.investwise_india.chatbot.RetrofitInstance
import com.investwise_india.model.InvestmentData
import com.investwise_india.model.MutualFund
import com.investwise_india.model.MutualFundCategories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.investwise_india.chatbot.Constants.apiKey

class ChatViewModel : ViewModel() {

    val messageList by lazy {
        mutableStateListOf<MessageModel>()
    }

    private var _fundMetaList = MutableStateFlow<List<FundMeta>>(emptyList())
    val fundMetaList = _fundMetaList.asStateFlow()

    val generativeModel : GenerativeModel = GenerativeModel(
        modelName = "gemini-2.0-flash",
        apiKey= apiKey
    )

    init {
        fetch()
    }

    fun fetch() {
        viewModelScope.launch {
            MutualFundCategories.categories.forEach {
                mutualFundCategory(
                    category = it,
                    selectedSubcategory = null
                ).forEach {
                    val response = RetrofitInstance.api.getLatestData(it)
                    _fundMetaList.value = _fundMetaList.value + response.meta
                    Log.d("Fetch", response.meta.toString())
                }
            }
        }
    }

//    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun sendMessage(question : String){
        viewModelScope.launch {

            try{
                withContext(Dispatchers.IO) {
                    val systemPrompt = generateGeminiPrompt(_fundMetaList.value)

                    val chat = generativeModel.startChat(
                        history = listOf(
                            content(role = "user") { text(systemPrompt) }
                        ) + messageList.map {
                            content(it.role) { text(it.message) }
                        }
                    )
                    messageList.add(MessageModel(question, "user"))
                    messageList.add(MessageModel("Thinking....", "model"))

                    val response = chat.sendMessage(question)
                    if (messageList.isNotEmpty())
                        messageList.removeAt(messageList.lastIndex)
                    messageList.add(MessageModel(response.text.toString(), "model"))
                }
            } catch (e : Exception){
                if (messageList.isNotEmpty())
                    messageList.removeAt(messageList.lastIndex)
                messageList.add(MessageModel("Error : "+e.message.toString(),"model"))
            }
        }
    }

    fun fetchData(Data : InvestmentData , mutualFund : MutualFund){
        viewModelScope.launch {
            val FixedData : String = Data.fixedReturnOptions.toString()
            val AbsoluteData : String = Data.absoluteReturnOptions.toString()
        }
    }
}


