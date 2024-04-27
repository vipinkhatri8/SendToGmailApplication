package com.example.sendpictureemail

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.example.sendtogmailapplication.databinding.ActivityMainBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private val REQUEST_CAMERA_PERMISSION = 1001
    private val REQUEST_IMAGE_CAPTURE = 1002
    private val REQUEST_IMAGE_SELECT = 1003

    private var currentPhotoPath: String? = null
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)



        // Request camera permission if not granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        }





        // Button click to send email
        binding.btnSendEmail.setOnClickListener {
            sendEmail()
        }
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        }

        binding.btnSelectImage.setOnClickListener {
            dispatchSelectPictureIntent()

        }




    }









    private fun dispatchSelectPictureIntent() {
        val selectPictureIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        selectPictureIntent.type = "image/*"
        startActivityForResult(selectPictureIntent, REQUEST_IMAGE_SELECT)
    }
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchSelectPictureIntent()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            // Image captured and saved to file, you can do something with the file here if needed
        } else if (requestCode == REQUEST_IMAGE_SELECT && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                // Do something with the selected image URI
                currentPhotoPath = uri.toString()
            }
        }
    }


    private fun sendEmail() {
        val contentUri = currentPhotoPath?.let { Uri.parse(it) }

        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            val to = arrayOf("vipinkhatri444@gmail.com")
            putExtra(Intent.EXTRA_EMAIL, to)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            putExtra(Intent.EXTRA_STREAM, contentUri)
            putExtra(Intent.EXTRA_SUBJECT, "Subject of Email")
            putExtra(Intent.EXTRA_TEXT, "Body of Email")
        }
        startActivity(Intent.createChooser(emailIntent, "Send email using..."))
    }

}