package com.example.tranquitaskapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import com.example.tranquitaskapp.R
import com.example.tranquitaskapp.firebase.MyFirebase
import android.util.Log
import android.widget.Toast
import com.example.tranquitaskapp.data.User
import com.google.firebase.firestore.FieldValue


class AddFriend : Fragment() {
    private val db = MyFirebase.getFirestoreInstance()

    private fun addFriend(view : View) {
        val email = view.findViewById<EditText>(R.id.mail).text.toString()

        db.collection("user").whereEqualTo("email", email).get().addOnSuccessListener { documents ->
            if (documents.documents.isNotEmpty()) {
                val friend = documents.documents[0].reference

                val test = friend.update("demandes", FieldValue.arrayUnion(User.ref))
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Error adding item to the array", e)
                }
            }
            else {
                Toast.makeText(this.context, getString(R.string.friend_not_exist), Toast.LENGTH_SHORT).show()
            }

        }
        .addOnFailureListener { exception ->
            // Gérer les erreurs éventuelles
            Log.e("ERROR", "Erreur lors de la récupération du user : $exception")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_friend, container, false)

        view.findViewById<ImageView>(R.id.search).setOnClickListener {
            addFriend(view)
        }

        return view
    }
}