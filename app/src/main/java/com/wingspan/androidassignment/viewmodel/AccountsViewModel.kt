package com.wingspan.androidassignment.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wingspan.androidassignment.api.BaseURLProvider
import com.wingspan.androidassignment.model.AccountsResponseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.regex.Pattern

class AccountsViewModel:ViewModel() {

    private val _contactsResponceSuccess = MutableLiveData<List<AccountsResponseModel>?>()
    val contactsResponceSuccess: LiveData<List<AccountsResponseModel>?> get() = _contactsResponceSuccess

    private val _contactsError = MutableLiveData<String?>()
    val contactsError: LiveData<String?> get() = _contactsError

    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun contactsApi(){
        _isLoading.value=true
        Log.d("loginApi","loginApi response--->.")
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    BaseURLProvider.create().getAccount()
                }

                Log.d("AccountsViewModel", "Response: ${response.body()}")

                if (response.isSuccessful && response.body() != null) {
                    val jsonString = response.body()?.removeSurrounding("<string>", "</string>")  // Remove XML tags
                    val listType = object : TypeToken<List<AccountsResponseModel>>() {}.type
                    val accountList: List<AccountsResponseModel> = Gson().fromJson(jsonString, listType)

                    _contactsResponceSuccess.postValue(accountList)
                } else {
                    _contactsError.postValue("Error: ${response.code()}")
                }
            } catch (e: IOException) {
                Log.e("NetworkError", "Network issue: ${e.message}")
                _contactsError.postValue("Network issue, please try again.")
            } catch (e: Exception) {
                Log.e("ParsingError", "Error: ${e.message}")
                _contactsError.postValue("Failed to fetch data: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }


    }
}