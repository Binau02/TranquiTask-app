package com.example.tranquitaskapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tranquitaskapp.adapter.CategoryRowAdapter
import com.example.tranquitaskapp.firebase.MyFirebase
import com.example.tranquitaskapp.adapter.FriendsRowAdapter
import com.example.tranquitaskapp.data.CategoryModel
import com.example.tranquitaskapp.data.FriendsModel
import com.example.tranquitaskapp.navigation.BottomBarVisibilityListener
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot
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
//    private val listFriendsModel = mutableListOf<FriendsModel>(
//        FriendsModel("Pseudo 1",R.drawable.arbre_removebg),
//        FriendsModel("Pseudo 2",R.drawable.leaderboard_icon),
//        FriendsModel("Pseudo 3",R.drawable.or),
//        FriendsModel("Pseudo 4",R.drawable.leaderboard_icon),
//        FriendsModel("Pseudo 5",R.drawable.arbre_removebg),
//        FriendsModel("Pseudo 6",R.drawable.add),
//        FriendsModel("Pseudo 7",R.drawable.or),
//        FriendsModel("Pseudo 8",R.drawable.leaderboard_icon),
//        FriendsModel("Pseudo 9",R.drawable.add),
//        FriendsModel("Pseudo 10",R.drawable.arbre_removebg),
//    )
    private val listFriendsModel = mutableListOf<FriendsModel>()

    private var bottomBarListener: BottomBarVisibilityListener? = null

    suspend fun getFriends() {
        val categories : MutableList<Pair<DocumentReference?, MutableList<Int>>> = mutableListOf()

        // récupérer l'utilisateur
        try {
            val friends = withContext(Dispatchers.IO) {
                Tasks.await(db.collection("ami").get())
            }
            for (friend in friends) {
//                Log.d("TEST", "friend $friend ${friend.id}")
                val ami1 = friend.getDocumentReference("ami1")
                val ami2 = friend.getDocumentReference("ami2")
//                Log.d("TEST", "amis $ami1 $ami2")
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
                if (ami1Doc != null && ami2Doc != null) {
                    Log.d("TEST", "ids ${ami1Doc.id} ${ami2Doc.id}")
                }
                if (ami1Doc != null && ami1Doc.id == "ZLsTpfBISeRYTUbDXuJA") {
                    addFriend(ami2Doc)
                }
                if (ami2Doc != null && ami2Doc.id == "ZLsTpfBISeRYTUbDXuJA") {
                    addFriend(ami1Doc)
                }
            }
            rv.adapter = FriendsRowAdapter(listFriendsModel)
        } catch (e: Exception) {
            Log.e("ERROR", "Error finding friend: $e")
        }
    }

    suspend fun addFriend(friendDoc : DocumentSnapshot?) {
        val name = friendDoc?.getString("username")
        if (name != null){
            listFriendsModel.add(FriendsModel(name, R.drawable.or))
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BottomBarVisibilityListener) {
            bottomBarListener = context
        }
        bottomBarListener?.setBottomBarVisibility(this)
    }

    fun onClickFriends(){
        Toast.makeText(this.context, "Le bouton Amis a été cliqué !", Toast.LENGTH_SHORT).show()
    }
    fun onClickNewFriend(){
        Toast.makeText(this.context, "Le bouton Demandes d'ami a été cliqué !", Toast.LENGTH_SHORT).show()
    }
    fun onClickAddFriend(){
        Toast.makeText(this.context, "Le bouton Ajouter un ami a été cliqué !", Toast.LENGTH_SHORT).show()
    }

    /*
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

     */

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_friends, container, false)
        rv = view.findViewById(R.id.rv_friend)
        val buttonFriends = view.findViewById<Button>(R.id.friendButton)
        val buttonNewFriend = view.findViewById<Button>(R.id.newFriendButton)
        val addFriends = view.findViewById<ImageView>(R.id.add_friend)

        buttonFriends.setOnClickListener {
            onClickFriends()
        }
        buttonNewFriend.setOnClickListener {
            onClickNewFriend()
        }
        addFriends.setOnClickListener {
            onClickAddFriend()
        }

        lifecycleScope.launch {
            getFriends()
        }

        rv.layoutManager = LinearLayoutManager(requireContext())
//        rv.adapter = FriendsRowAdapter(listFriendsModel) // Initialisez avec une liste vide ou vos données

        //loadRecyclerViewData(rv) // Chargez les données dans la RecyclerView

        return view
        // Inflate the layout for this fragment
    }
/*
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Friends.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Friends().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

 */
}
