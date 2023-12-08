package com.example.tranquitaskapp.fragment

import android.content.Context
import android.os.Bundle
import android.transition.Slide
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tranquitaskapp.R
import com.example.tranquitaskapp.data.User
import com.example.tranquitaskapp.firebase.MyFirebase
import com.example.tranquitaskapp.adapter.FriendsRowAdapter
import com.example.tranquitaskapp.data.FriendsModel
import com.example.tranquitaskapp.interfaces.BottomBarVisibilityListener
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
// private const val ARG_PARAM1 = "param1"
// private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Friends.newInstance] factory method to
 * create an instance of this fragment.
 */
class Friends : Fragment() {
    // TODO: Rename and change types of parameters
    // private var param1: String? = null
    // private var param2: String? = null

    private val db = MyFirebase.getFirestoreInstance()
    private lateinit var rv: RecyclerView

    private val globalFriends = mutableListOf<FriendsModel>()

    private var bottomBarListener: BottomBarVisibilityListener? = null


    private var friendsSelected : Boolean = true
    private val globalDemandes : MutableList<FriendsModel> = mutableListOf()
    private lateinit var user : DocumentSnapshot
    private lateinit var demandes : MutableList<DocumentReference>

    private var colorPrimary: Int = 0
    private var colorDark: Int = 0
    private suspend fun getFriends() {
        // récupérer les amis
        try {
            val friends = withContext(Dispatchers.IO) {
                Tasks.await(db.collection("ami").get())
            }
            for (friend in friends) {
                val ami1 = friend.getDocumentReference("ami1")
                val ami2 = friend.getDocumentReference("ami2")
                var ami1Doc : DocumentSnapshot? = null
                var ami2Doc : DocumentSnapshot? = null
                try {
                    if (ami1 != null) {
                        ami1Doc = withContext(Dispatchers.IO) {
                            Tasks.await(ami1.get())
                        }
                    }
                } catch (e: Exception) {
                    Log.e("ERROR", "Error getting ami1 : $e")
                }
                try {
                    if (ami2 != null) {
                        ami2Doc = withContext(Dispatchers.IO) {
                            Tasks.await(ami2.get())
                        }
                    }
                } catch (e: Exception) {
                    Log.e("ERROR", "Error getting ami2 : $e")
                }
                if (ami1Doc != null && ami1Doc.id == User.id) {
                    addFriend(ami2Doc)
                }
                if (ami2Doc != null && ami2Doc.id == User.id) {
                    addFriend(ami1Doc)
                }
            }
            setFriends()
        } catch (e: Exception) {
            Log.e("ERROR", "Error finding friend: $e")
        }
    }

    private suspend fun getDemandes() {
        try {
            user = withContext(Dispatchers.IO) {
                Tasks.await(db.collection("user").document(User.id).get())
            }
            demandes = user.get("demandes") as MutableList<DocumentReference>
            for (demandeDoc in demandes) {
                try {
                    val demande = withContext(Dispatchers.IO) {
                        Tasks.await(demandeDoc.get())
                    }
                    globalDemandes.add(
                        FriendsModel(
                            pseudo = demande.getString("username") ?: "",
                            avatar = demande.getString("profile_picture") ?: "",
                            ref = demandeDoc
                        )
                    )
                } catch (e : Exception) {
                    Log.e("ERROR", "Error finding demande : $e")
                }
            }
        } catch (e: Exception) {
            Log.e("ERROR", "Error finding user: $e")
        }
        if (!friendsSelected) {
            setDemandes()
        }
    }

    private fun addFriend(friendDoc : DocumentSnapshot?) {
        val name = friendDoc?.getString("username")
        val pp = friendDoc?.getString("profile_picture")
        if (name != null && pp != null){
            globalFriends.add(FriendsModel(name, pp, friendDoc.reference))
        }
    }

    private fun setFriends() {
        rv.adapter = FriendsRowAdapter(globalFriends, this)
    }

    private fun setDemandes() {
        rv.adapter = FriendsRowAdapter(globalDemandes, this, false)
    }

    fun acceptNewFriend(position: Int) {
        val newFriend = hashMapOf(
            "ami1" to User.ref,
            "ami2" to globalDemandes[position].ref
        )
        db.collection("ami").add(newFriend)
        globalFriends.add(globalDemandes[position])
        deleteDemande(position)
    }

    fun denyNewFriend(position: Int) {
        deleteDemande(position)
    }

    private fun deleteDemande(position: Int) {
        demandes.remove(globalDemandes[position].ref)
        user.reference.update("demandes", demandes).addOnFailureListener {e ->
            Log.d("ERROR", "Error updating demandes of user : $e")
        }
        globalDemandes.removeAt(position)
        setDemandes()
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        colorPrimary = ContextCompat.getColor(requireContext(), R.color.my_primary_light)
        colorDark = ContextCompat.getColor(requireContext(), R.color.my_dark)
        if (context is BottomBarVisibilityListener) {
            bottomBarListener = context
        }
        bottomBarListener?.setBottomBarVisibility(this)
    }

    private fun onClickFriends(){
//        Toast.makeText(this.context, "Le bouton Amis a été cliqué !", Toast.LENGTH_SHORT).show()
        if (!friendsSelected) {
            friendsSelected = true
            setFriends()
        }
    }
    private fun onClickNewFriend(){
//        Toast.makeText(this.context, "Le bouton Demandes d'ami a été cliqué !", Toast.LENGTH_SHORT).show()
        if (friendsSelected) {
            friendsSelected = false
            setDemandes()
        }
    }
    private fun onClickAddFriend(){
//        Toast.makeText(this.context, "Le bouton Ajouter un ami a été cliqué !", Toast.LENGTH_SHORT).show()
        val fragment = AddFriend(globalFriends, globalDemandes)
        val slideUp = Slide(Gravity.TOP)
        slideUp.duration = 150 // Durée de l'animation en millisecondes
        fragment.enterTransition = slideUp
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameLayout, fragment)?.commit()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_friends, container, false)
        rv = view.findViewById(R.id.rv_friend)
        val buttonFriends = view.findViewById<TextView>(R.id.tvFriends)
        val buttonNewFriend = view.findViewById<TextView>(R.id.tvNewFriends)
        val addFriends = view.findViewById<ImageView>(R.id.add_friend)

        buttonFriends.setOnClickListener {
            buttonFriends.setTextColor(colorDark)
            buttonFriends.setBackgroundColor(colorPrimary)
            buttonNewFriend.setTextColor(colorPrimary)
            buttonNewFriend.setBackgroundColor(colorDark)
            onClickFriends()
        }
        buttonNewFriend.setOnClickListener {
            buttonFriends.setTextColor(colorPrimary)
            buttonFriends.setBackgroundColor(colorDark)
            buttonNewFriend.setTextColor(colorDark)
            buttonNewFriend.setBackgroundColor(colorPrimary)
            onClickNewFriend()
        }
        addFriends.setOnClickListener {
            onClickAddFriend()
        }

        lifecycleScope.launch {
            getFriends()
        }
        lifecycleScope.launch {
            getDemandes()
        }

        rv.layoutManager = LinearLayoutManager(requireContext())
//        rv.adapter = FriendsRowAdapter(listFriendsModel) // Initialisez avec une liste vide ou vos données

        //loadRecyclerViewData(rv) // Chargez les données dans la RecyclerView

        return view
        // Inflate the layout for this fragment
    }
}