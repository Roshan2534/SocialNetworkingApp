package com.example.capstone


import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.sql.SQLException
import kotlin.Exception


private const val TAG = "SignupActivity"
class SignupActivity : AppCompatActivity() {

    private val cameraPermissionRequestCode = 123
    private var clickedImageView: ImageView? = null


    private val connection by lazy {
        ConnectionManager.rdsconnection
    }


    private val s3Client by lazy {
        ConnectionManager.s3connection
    }


    private val requestCameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            openCamera()
        } else {
            Log.d(TAG, "Permission denied")
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

    }

    fun checkPermissions(view: View) {
        clickedImageView = view as ImageView
        if (hasCameraPermission()) {
            openCamera()
        } else {
            requestCameraPermission()
        }
    }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        try {
            startActivityForResult(cameraIntent, cameraPermissionRequestCode)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                this,
                 "Error: " + e.localizedMessage, Toast.LENGTH_SHORT
            )
        }
    }

    fun uploadProfile(view: View) {
        val firstName = findViewById<EditText>(R.id.editTextFirstName).text.toString()
        val lastName = findViewById<EditText>(R.id.editTextLastName).text.toString()
        val email = findViewById<EditText>(R.id.editTextEmail).text.toString()
        val password = findViewById<EditText>(R.id.editTextPassword).text.toString()

        val passwordHash = PasswordUtils.hashPassword(password)

        val loadingOverlay = showLoadingOverlay()

        GlobalScope.launch(Dispatchers.IO) {
            try {

                val userId = uploadtoRDS(firstName, lastName, email, passwordHash)


                for (i in 1..2) {
                    for (j in 1..3) {

                        val imageViewId =
                            resources.getIdentifier("imageView$i$j", "id", packageName)
                        val imageView = findViewById<ImageView>(imageViewId)

                        val imageBitmap = (imageView.drawable as BitmapDrawable).bitmap
                        val byteArray = getByteArray(imageBitmap)
                        val bucketKey = "user_images/user${userId}_image$i$j.jpg"

                        s3Client.putObject(
                            "profilephotoscapstone",
                            bucketKey,
                            ByteArrayInputStream(byteArray),
                            null
                        )

                    }


                }
                val photoUrls = (1..2).flatMap { i ->
                    (1..3).map { j ->
                        "https://profilephotoscapstone.s3-us-west-1.amazonaws.com/user_images/user${userId}_image$i$j.jpg"
                    }
                }.toTypedArray()

                var response = updatePhotoUrlsInRDS(userId, photoUrls)

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                runOnUiThread{
                    hideLoadingOverlay(loadingOverlay)
                }
            }
        }
    }

    private fun showLoadingOverlay(): FrameLayout {
        // Disable user interactions
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )

        // Create a transparent overlay with a ProgressBar and a TextView
        val overlay = FrameLayout(this)
        overlay.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        overlay.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))

        val inflater = LayoutInflater.from(this)
        val loadingView = inflater.inflate(R.layout.loading_overlay, null)
        overlay.addView(loadingView)

        // Add the overlay to the content view
        addContentView(
            overlay,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        return overlay
    }

    private fun hideLoadingOverlay(overlay: FrameLayout) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

        (overlay.parent as? ViewGroup)?.removeView(overlay)


        Toast.makeText(applicationContext, "Profile created successfully!", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()

    }



    fun getByteArray(imagebitmap: Bitmap): ByteArray{
        val stream = ByteArrayOutputStream()
        imagebitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }

    fun updatePhotoUrlsInRDS(id: Int, urls: Array<String>): Int {
        Log.d(TAG, "updatePhotoUrlsInRDS: Inside updatephtotos")
        try {
            val query = "UPDATE public.profiles SET image_links = ARRAY[${"?, ".repeat(urls.size).removeSuffix(", ")}] WHERE id = ?;"
            val preparedStatement = connection.prepareStatement(query)

            // Set values for the parameters
            for ((index, url) in urls.withIndex()) {
                preparedStatement.setString(index + 1, url)
            }
            preparedStatement.setInt(urls.size + 1, id)

            // Execute the update
            val result = preparedStatement.executeUpdate()
            Log.d(TAG, "updatePhotoUrlsInRDS: result is ${result}")
            return result
        } catch (e: SQLException) {
            // Handle the exception appropriately
            Log.d(TAG, "updatePhotoUrlsInRDS: Exception happened")
            e.printStackTrace()
            return -1
        }

    }

    fun uploadtoRDS(firstName: String, lastName: String, email: String, password: String): Int {

        val query = "INSERT INTO public.profiles (first_name, last_name, email, password) VALUES (?,?,?,?) RETURNING id"


        val preparedStatement = connection.prepareStatement(query)

        preparedStatement.setString(1, firstName)
        preparedStatement.setString(2, lastName)
        preparedStatement.setString(3, email)
        preparedStatement.setString(4, password)

        val result = preparedStatement.executeQuery()

        if (result.next()) {
            val generatedId = result.getInt("id")
            return generatedId
        } else {
            return -1
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == cameraPermissionRequestCode && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            clickedImageView?.setImageBitmap(imageBitmap)
            clickedImageView = null
        }
    }

}