package com.example.capstone

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class ProfileFragment : Fragment() {

    private lateinit var firstNameTextView: TextView
    private lateinit var lastNameTextView: TextView
    private lateinit var imagesRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view  = inflater.inflate(R.layout.fragment_profile, container, false)
        firstNameTextView = view.findViewById(R.id.firstNameTextView)
        lastNameTextView = view.findViewById(R.id.lastNameTextView)
        imagesRecyclerView = view.findViewById(R.id.imagesRecyclerView)

        return view

    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)

//        val userEmail = sharedPreferences.getString("user_email", null)
//
//        if (!userEmail.isNullOrBlank()) {
//            val user = getUserDetails(userEmail)
//            updateUI(user)
//        } else {
//
//        }
//
//    }


}