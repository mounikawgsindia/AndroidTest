package com.wingspan.androidassignment.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wingspan.androidassignment.database.AccountsDao
import com.wingspan.androidassignment.model.AccountsPoint
import com.wingspan.androidassignment.model.RoomResponseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RoomViewModel : ViewModel() {

    private val _accountsList = MutableLiveData<List<AccountsPoint>>()
    val accountsList: LiveData<List<AccountsPoint>> get() = _accountsList



    // Fetch all accounts from the database and update LiveData
    fun loadAllAccounts(accountsDao: AccountsDao) {
        viewModelScope.launch(Dispatchers.IO) {
            val accounts = accountsDao.getGeoFencingPoints()
            _accountsList.postValue(accounts) // Post value to update UI
        }
    }


}