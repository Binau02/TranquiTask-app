package com.example.tranquitaskapp.fragment

import android.os.Bundle
import android.transition.Slide
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import com.example.tranquitaskapp.R
import com.example.tranquitaskapp.firebase.MyFirebase
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import com.example.tranquitaskapp.data.FriendsModel


class AddFriend(val friends : MutableList<FriendsModel>, val demandes : MutableList<FriendsModel>) : Fragment() {
    private val db = MyFirebase.getFirestoreInstance()

    private fun addFriend(view : View) {
        val email = view.findViewById<EditText>(R.id.mail).text.toString()

        db.collection("user").whereEqualTo("email", email).get().addOnSuccessListener { documents ->
            if (documents.documents.isNotEmpty()) {
                val friend = documents.documents[0].reference

                Log.d("TEST", "$friends")
                if (friends.find { it.ref == friend } == null) {
                    if (demandes.find { it.ref == friend } == null) {
                        val fragment = ProfileAddFriend(friend)
                        val transaction = fragmentManager?.beginTransaction()
                        transaction?.replace(R.id.frameLayout, fragment)?.commit()
                    }
                    else {
                        Toast.makeText(this.context, getString(R.string.already_asked), Toast.LENGTH_SHORT).show()
                    }
                }
                else {
                    Toast.makeText(this.context, getString(R.string.already_friend), Toast.LENGTH_SHORT).show()
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

    private fun replaceFragment(fragment: Fragment){
        val slideUp = Slide(Gravity.BOTTOM)
        slideUp.duration = 150 // Durée de l'animation en millisecondes
        fragment.enterTransition = slideUp
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameLayout, fragment)?.commit()
    }

    private fun onClickBack(){
        replaceFragment(Friends())
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_friend, container, false)

        val buttonBack = view.findViewById<ImageView>(R.id.back)

        view.findViewById<ImageView>(R.id.search).setOnClickListener {
            addFriend(view)
        }

        buttonBack.setOnClickListener {
            onClickBack()
        }

        return view
    }
}