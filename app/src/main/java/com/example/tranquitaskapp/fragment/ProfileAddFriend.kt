package com.example.tranquitaskapp.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.tranquitaskapp.R
import com.example.tranquitaskapp.data.User
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ProfileAddFriend (val friendDoc : DocumentReference) : Fragment() {
    private var pseudo = ""
    private var pp = ""

    private lateinit var view : View

    private suspend fun getFriend() {
        try {
            val friend = withContext(Dispatchers.IO) {
                Tasks.await(friendDoc.get())
            }
            pseudo = friend.getString("username") ?: ""
            pp = friend.getString("profile_picture") ?: ""
            setFriend()
        } catch (e : Exception) {
            Log.e("ERROR", "Error getting friend : $e")
        }
    }

    private fun setFriend() {
        view.findViewById<TextView>(R.id.tv_pseudo).text = pseudo
        if (pp != "") {
            Glide.with(this)
                .load(pp)
                .into(view.findViewById(R.id.profileimage))
        }
    }

    private fun addFriend() {
        friendDoc.update("demandes", FieldValue.arrayUnion(User.ref))
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error adding item to the array", e)
            }
        val fragment = Friends()
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameLayout, fragment)?.commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        view = inflater.inflate(R.layout.fragment_profile_add_friend, container, false);

        view.findViewById<Button>(R.id.button_modif).setOnClickListener {
            addFriend()
        }

        view.findViewById<Button>(R.id.back2).setOnClickListener{
            val fragment = Friends()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.frameLayout, fragment)?.commit()
        }

        lifecycleScope.launch {
            getFriend()
        }

        return view;
    }
}