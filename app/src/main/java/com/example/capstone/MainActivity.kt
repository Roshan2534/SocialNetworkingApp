package com.example.capstone

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.SQLException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.PreparedStatement

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private val connection by lazy {
        ConnectionManager.rdsconnection
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

        val emailEditText = findViewById<EditText>(R.id.editTextEmail)
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)
        val loginButton = findViewById<Button>(R.id.loginbutton)

        loginButton.setOnClickListener{
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()


            lifecycleScope.launch {

                if (isValidLogin(email,password)) {
                    saveUserInfo(email)

                    startActivity(Intent(this@MainActivity, Wall::class.java))
                    finish()
                } else {

                    Toast.makeText(
                        this@MainActivity,
                        "User not found. Please check credentials.",
                        Toast.LENGTH_SHORT
                    ).show()


                }

            }

        }

        val textViewSignup = findViewById<TextView>(R.id.textViewSignup)
        val text = SpannableString("Not a user? Signup.")
        val clickableSpan = object: ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(this@MainActivity, SignupActivity::class.java))
            }

        }

        text.setSpan(clickableSpan, 12, 19, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        textViewSignup.text = text
        textViewSignup.movementMethod = LinkMovementMethod.getInstance()


    }

    private suspend fun isValidLogin(email:String, password:String): Boolean{
        return true
//        val newpassword = PasswordUtils.hashPassword(password)
//        return try {
//            withContext(Dispatchers.IO) {
//                val storedHash = getStoredHash(email)
//
//                storedHash != null && PasswordUtils.verifyPassword(newpassword, storedHash)
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            false
//        }
    }

    private fun getStoredHash(email: String): String? {
        val query =  "SELECT password FROM public.profiles WHERE email = ?"

        try {
            val preparedStatement: PreparedStatement = connection.prepareStatement(query)
            preparedStatement.setString(1, email)

            val resultSet = preparedStatement.executeQuery()

            return if (resultSet.next()) {
                resultSet.getString("password")
            } else {
                null
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            return null
        }
    }


    private fun saveUserInfo(email: String) {
        val editor = sharedPreferences.edit()
        editor.putString("user_email", email)
        editor.apply()
    }
}