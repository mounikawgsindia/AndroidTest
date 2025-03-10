package com.wingspan.androidassignment.view.activity

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.wingspan.androidassment.R
import com.wingspan.androidassment.databinding.ActivityPdfactivityBinding
import java.io.File

class PDFActivity : AppCompatActivity() {
    lateinit var binding:ActivityPdfactivityBinding

    private val pdfUrl = "https://fssservices.bookxpert.co/GeneratedPDF/Companies/nadc/2024-2025/BalanceSheet.pdf"
    private val FILE_NAME = "BalanceSheet.pdf"
    private val PERMISSION_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityPdfactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUI()
    }

    private fun setUI() {
        binding.apply {


            btnDownloadPdf.setOnClickListener {
                if (checkStoragePermission()) {
                    downloadPdf()
                }
            }

            btnOpenDownloadedPdf.setOnClickListener {
                openDownloadedPdf()
            }
        }
    }

    @SuppressLint("IntentReset")
    private fun openPdfUrl(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
            type = "application/pdf"
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(context, "No PDF viewer found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkStoragePermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                    this,android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE
                )
                return false
            }
        }
        return true
    }

    private fun downloadPdf() {
        val request = DownloadManager.Request(Uri.parse(pdfUrl)).apply {
            setTitle("Downloading PDF")
            setDescription("Balance Sheet PDF is being downloaded")
            setDestinationInExternalFilesDir(this@PDFActivity, Environment.DIRECTORY_DOWNLOADS, FILE_NAME)
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        }

        val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        manager.enqueue(request)

        Toast.makeText(this, "Download started...", Toast.LENGTH_SHORT).show()
    }

    private fun openDownloadedPdf() {
        val file = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), FILE_NAME)
        if (!file.exists()) {
            Toast.makeText(this, "File not found. Download it first.", Toast.LENGTH_SHORT).show()
            return
        }

        val uri = FileProvider.getUriForFile(this, "$packageName.provider", file)

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "No PDF viewer found", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downloadPdf()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}