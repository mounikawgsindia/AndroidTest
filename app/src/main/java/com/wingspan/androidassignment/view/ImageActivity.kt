package com.wingspan.androidassignment.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.wingspan.androidassignment.extensions.Singleton.setDebouncedClickListener
import com.wingspan.androidassment.R
import com.wingspan.androidassment.databinding.ActivityImageBinding
import com.wingspan.androidassment.databinding.AlertdialogImageBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ImageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImageBinding
    private var binding1: AlertdialogImageBinding? = null
    private var photoURI: Uri? = null
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    private lateinit var currentPhotoPath: String

    companion object {
        private const val REQUEST_PERMISSION_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkAndRequestPermissions()
        initializeLaunchers()
        setupUI()
    }

    private fun initializeLaunchers() {
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            handleImageResult(uri)
        }

        takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) handleImageResult(photoURI)
        }
    }

    private fun setupUI() {
        binding.imageIv.setOnClickListener { dialogForChoosingImageType() }
        binding.cv.setDebouncedClickListener(){
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun handleImageResult(imageUri: Uri?) {
        imageUri?.let {
            Glide.with(this).load(it).into(binding.imageIv)
        }
    }

    private fun dialogForChoosingImageType() {
        binding1 = AlertdialogImageBinding.inflate(layoutInflater)
        val alertDialog = AlertDialog.Builder(this).setView(binding1!!.root).create()

        binding1!!.apply {
            takePhoto.setOnClickListener {
                cameraPicCaptureIntent()
                alertDialog.dismiss()
            }
            chooseFromGallery.setOnClickListener {
                pickImageLauncher.launch("image/*")
                alertDialog.dismiss()
            }
            cancel.setOnClickListener { alertDialog.dismiss() }
        }
        alertDialog.show()
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun cameraPicCaptureIntent() {
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException) {
            ex.printStackTrace()
            null
        }

        photoFile?.let {
            photoURI = FileProvider.getUriForFile(this, "${applicationContext.packageName}.fileprovider", it)
            takePictureLauncher.launch(photoURI!!)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun checkAndRequestPermissions(): Boolean {
        val permissions = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.CAMERA)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissions.toTypedArray(), REQUEST_PERMISSION_CODE)
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Some permissions are denied. Please enable them in settings", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

