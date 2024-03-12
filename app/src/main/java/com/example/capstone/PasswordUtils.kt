package com.example.capstone

import android.util.Log
import com.toxicbakery.bcrypt.Bcrypt

private const val TAG = "PasswordUtils"
object PasswordUtils {
    fun hashPassword(password: String): String {
        Log.d(TAG, "hashPassword: Before Hash ${password}")
        val hashedPassword = Bcrypt.hash(password, 12)
        Log.d(TAG, "hashPassword: ${hashedPassword}")
        return hashedPassword.toString()
    }

    fun verifyPassword(candidate: String, storedPassword: String): Boolean {
        Log.d(TAG, "verifyPassword: Candidate: ${candidate}, storedPass: ${storedPassword}")
        Log.d(TAG, "Bcrypt: ${Bcrypt.verify(candidate, storedPassword.toByteArray())}")
        return Bcrypt.verify(candidate, storedPassword.toByteArray())
    }
}