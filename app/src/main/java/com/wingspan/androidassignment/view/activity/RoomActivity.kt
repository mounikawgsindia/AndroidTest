package com.wingspan.androidassignment.view.activity

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.wingspan.androidassignment.adapter.RoomAccountsAdapter
import com.wingspan.androidassignment.database.AccountsDataBase
import com.wingspan.androidassignment.extensions.Singleton.setDebouncedClickListener
import com.wingspan.androidassignment.model.AccountsPoint
import com.wingspan.androidassignment.viewmodel.RoomViewModel
import com.wingspan.androidassment.databinding.ActivityMain2Binding

class RoomActivity : AppCompatActivity() {
    lateinit  var binding:ActivityMain2Binding
    private lateinit var accountsAdapter: RoomAccountsAdapter
    private var accountsList=ArrayList<AccountsPoint>()
    private val viewModel: RoomViewModel by viewModels()
    private val RECORD_AUDIO_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        requestAudioPermission()

        setRecycleView()
        setUI()
        updateRecycleView()
        setObservable()


    }

    private fun requestAudioPermission() {
        if (!checkAudioPermission()) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.RECORD_AUDIO),
                RECORD_AUDIO_REQUEST_CODE
            )
        }
    }

    private fun checkAudioPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
           this,
            android.Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun updateRecycleView() {
        val accountsDao = AccountsDataBase.getDatabase(this).accountsPoints()
        viewModel.loadAllAccounts(accountsDao)
    }

    private fun setUI() {
        binding.apply {
            backLl.setDebouncedClickListener(){
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == RECORD_AUDIO_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun setObservable() {
        binding.apply {
            viewModel.accountsList.observe(this@RoomActivity) { response ->
                Log.d("response","-->${response}")
                if(response!=null){
                    accountsList.clear()
                    accountsList = response as ArrayList<AccountsPoint>
                    accountsAdapter.updateData(accountsList)
                }

            }

        }
    }
    private fun setRecycleView() {
        binding.roomAccountsRv.apply {
            accountsAdapter= RoomAccountsAdapter(this@RoomActivity,accountsList,viewModel)
            layoutManager=
                LinearLayoutManager(this@RoomActivity, LinearLayoutManager.VERTICAL,false)
            adapter=accountsAdapter
        }
    }
}