package com.wingspan.androidassignment.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.wingspan.androidassignment.adapter.AccountsAdapter
import com.wingspan.androidassignment.database.AccountsDataBase
import com.wingspan.androidassignment.extensions.Singleton
import com.wingspan.androidassignment.extensions.Singleton.setDebouncedClickListener
import com.wingspan.androidassignment.model.AccountsPoint
import com.wingspan.androidassignment.model.AccountsResponseModel
import com.wingspan.androidassignment.viewmodel.AccountsViewModel
import com.wingspan.androidassment.databinding.ActivityAccountsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AccountsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAccountsBinding
    private val viewModel: AccountsViewModel by viewModels()
    private lateinit var notificationAdapter: AccountsAdapter
    var accountsList=ArrayList<AccountsResponseModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAccountsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setRecycleView()
        makeApiCall()
        setObservable()
        setUI()
    }

    private fun setObservable() {
        binding.apply {
            viewModel.contactsResponceSuccess.observe(this@AccountsActivity) { response ->
                if(response!=null){
                    accountsList.clear()
                    accountsList = response as ArrayList<AccountsResponseModel>
                    notificationAdapter.updateData(accountsList)
                }

            }
            viewModel.contactsError.observe(this@AccountsActivity) { error ->
              Toast.makeText(this@AccountsActivity,"${error}",Toast.LENGTH_SHORT).show()
            }
            viewModel.isLoading.observe(this@AccountsActivity) { isLoading ->
                Log.d("LoginActivity", "Loading status: $isLoading")
                binding.loader.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
    }

    private fun setUI() {
        binding.apply {
            backLl.setDebouncedClickListener(){
                onBackPressedDispatcher.onBackPressed()
            }
            storeDataBtn.setDebouncedClickListener {
                //save data into room db
                storeDataIntoBoomDataBase()

            }
            viewDataBtn.setDebouncedClickListener {
                val intent= Intent(this@AccountsActivity, RoomActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun storeDataIntoBoomDataBase() {
       // roomAccountsList.clear()
        val geoFencingDao = AccountsDataBase.getDatabase(this).accountsPoints()

        CoroutineScope(Dispatchers.IO).launch {
            geoFencingDao.deleteAllGeoFencingPoints()
            val roomAccountsList = accountsList.map {
                AccountsPoint(  // Change to AccountsPoint instead of RoomResponseModel
                    AccountName = it.ActName ?: "",
                    AccountId = it.actid ?: "",
                    alterName = ""
                )
            }
            geoFencingDao.insertAccountsPoint(roomAccountsList) // Now passing correct data type

            runOnUiThread {
                Toast.makeText(this@AccountsActivity, "Data stored successfully!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setRecycleView() {
        binding.accountsRv.apply {
            notificationAdapter= AccountsAdapter(this@AccountsActivity,accountsList,viewModel)
            layoutManager=
                LinearLayoutManager(this@AccountsActivity, LinearLayoutManager.VERTICAL,false)
            adapter=notificationAdapter
        }
    }
    private fun makeApiCall(){
        if(Singleton.isNetworkAvailable(this@AccountsActivity)){
            viewModel.contactsApi()
        }else{
            Singleton.showNetworkAlertDialog(this@AccountsActivity)
        }

    }
}