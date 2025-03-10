package com.wingspan.androidassignment.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels

import com.wingspan.androidassignment.extensions.Singleton.setDebouncedClickListener
import com.wingspan.androidassignment.viewmodel.AccountsViewModel
import com.wingspan.androidassment.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    val viewModel:AccountsViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUI()

    }

    @SuppressLint("SuspiciousIndentation")
    private fun setUI() {
        binding.apply {
            getAllCountsBtn.setDebouncedClickListener(){
              val  intent= Intent(this@MainActivity, AccountsActivity::class.java)
                startActivity(intent)
            }
            imageStoreBtn.setOnClickListener(){
                val intent =Intent(this@MainActivity, ImageActivity::class.java)
                startActivity(intent)
            }
            pdfBtn.setDebouncedClickListener {
                val intent =Intent(this@MainActivity, PDFActivity::class.java)
                startActivity(intent)
            }

        }
    }
}