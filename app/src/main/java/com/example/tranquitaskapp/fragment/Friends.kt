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
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tranquitaskapp.R
import com.example.tranquitaskapp.data.User
import com.example.tranquitaskapp.firebase.MyFirebase
import com.example.tranquitaskapp.adapter.FriendsRowAdapter
import com.example.tranquitaskapp.data.FriendsModel
import com.example.tranquitaskapp.interfaces.BottomBarVisibilityListener
import com.example.tranquitaskapp.ui.CustomPopup
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class Friends : Fragment() {

    private val db = MyFirebase.getFirestoreInstance()
    private lateinit var rv: RecyclerView

    private val globalFriends = mutableListOf<FriendsModel>()

    private lateinit var badge: TextView
    private lateinit var supprimer: ImageView

    private var bottomBarListener: BottomBarVisibilityListener? = null


    private var friendsSelected: Boolean = true
    private val globalDemandes: MutableList<FriendsModel> = mutableListOf()
    private lateinit var user: DocumentSnapshot
    private lateinit var demandes: MutableList<DocumentReference>

    private var colorPrimary: Int = 0
    private var colorDark: Int = 0

    private fun goToFriendsProfile(ref: DocumentReference){
        replaceFragment(ProfileOther(ref,true))

    }

    private fun replaceFragment(fragment: Fragment){
        val slideUp = Slide(Gravity.BOTTOM)
        slideUp.duration = 150 // Durée de l'animation en millisecondes
        fragment.enterTransition = slideUp
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameLayout, fragment)?.commit()
    }

    private suspend fun getFriends() {
        // récupérer les amis
        try {
            val friends = withContext(Dispatchers.IO) {
                Tasks.await(db.collection("ami").get())
            }
            for (friend in friends) {
                val ami1 = friend.getDocumentReference("ami1")
                val ami2 = friend.getDocumentReference("ami2")
                var ami1Doc: DocumentSnapshot? = null
                var ami2Doc: DocumentSnapshot? = null
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
                Tasks.await(User.ref!!.get())
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
                } catch (e: Exception) {
                    Log.e("ERROR", "Error finding demande : $e")
                }
            }
        } catch (e: Exception) {
            Log.e("ERROR", "Error finding user: $e")
        }
        if (!friendsSelected) {
            setDemandes()
        }
        badge.text = globalDemandes.size.toString()
    }

    private fun addFriend(friendDoc: DocumentSnapshot?) {
        val name = friendDoc?.getString("username")
        val pp = friendDoc?.getString("profile_picture")
        if (name != null && pp != null) {
            globalFriends.add(FriendsModel(name, pp, friendDoc.reference))
        }
    }

    private fun setFriends() {
        rv.adapter = FriendsRowAdapter(globalFriends, this){ friendDocumentReference ->
            goToFriendsProfile(friendDocumentReference)
        }
        if (globalDemandes.isNotEmpty()) {
            badge.visibility = View.VISIBLE
        } else {
            badge.visibility = View.INVISIBLE
        }
    }

    private fun setDemandes() {
        rv.adapter = FriendsRowAdapter(globalDemandes, this, false){ friendsReference ->
            goToFriendsProfile(friendsReference)

        }
        badge.visibility = View.INVISIBLE
    }

    fun acceptNewFriend(position: Int) {
        val newFriend = hashMapOf(
            "ami1" to User.ref,
            "ami2" to globalDemandes[position].ref
        )
        db.collection("ami").add(newFriend)
        globalFriends.add(globalDemandes[position])
        badge.text = globalDemandes.size.toString()
        deleteDemande(position)
    }

    fun removeFriend(position: Int) {
        this.context?.let {
            CustomPopup.showPopup(
                context = it,
                "Voulez-vous vraiment supprimer cet ami ?",
                object : CustomPopup.PopupClickListener {
                    override fun onPopupButtonClick() {
                        val friendRef = globalFriends[position].ref
                        val userRef = User.ref
                        val amiCollection = db.collection("ami")
                        amiCollection
                            .whereEqualTo("ami1", friendRef)
                            .whereEqualTo("ami2", userRef)
                            .get()
                            .addOnSuccessListener { querySnapshot ->
                                for (document in querySnapshot.documents) {
                                    // Supprimer chaque document trouvé
                                    amiCollection.document(document.id).delete()
                                        .addOnSuccessListener {
                                            Log.d("Friends", "Ami supprimé à la position")
                                        }
                                }
                            }

                        amiCollection
                            .whereEqualTo("ami2", friendRef)
                            .whereEqualTo("ami1", userRef)
                            .get()
                            .addOnSuccessListener { querySnapshot ->
                                for (document in querySnapshot.documents) {
                                    // Supprimer chaque document trouvé
                                    amiCollection.document(document.id).delete()
                                        .addOnSuccessListener {
                                            Log.d("Friends", "Ami supprimé à la position")
                                            globalFriends.removeIf {
                                                it.ref == friendRef
                                            }
                                            rv.adapter?.notifyDataSetChanged()
                                        }
                                }
                            }
                    }
                })
        }
    }

    fun denyNewFriend(position: Int) {
        deleteDemande(position)
    }

    private fun deleteDemande(position: Int) {
        demandes.remove(globalDemandes[position].ref)
        user.reference.update("demandes", demandes).addOnFailureListener { e ->
            Log.d("ERROR", "Error updating demandes of user : $e")
        }
        globalDemandes.removeAt(position)
        setDemandes()
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        val typedArray = requireContext().theme.obtainStyledAttributes(
            intArrayOf(android.R.attr.colorPrimary)
        )

        try {
            colorPrimary = typedArray.getColor(0, 0)
        } finally {
            typedArray.recycle()
        }
        val typedArrayDark = requireContext().theme.obtainStyledAttributes(
            intArrayOf(android.R.attr.colorPrimaryDark)
        )

        try {
            colorDark = typedArrayDark.getColor(0, 0)
        } finally {
            typedArrayDark.recycle()
        }

    }

    private fun onClickFriends() {
        if (!friendsSelected) {
            friendsSelected = true
            setFriends()
        }
    }

    private fun onClickNewFriend() {
        if (friendsSelected) {
            friendsSelected = false
            setDemandes()
        }
    }

    private fun onClickAddFriend() {
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
        badge = view.findViewById(R.id.notificationBadge)

        val buttonFriends = view.findViewById<TextView>(R.id.tvFriends)
        val buttonNewFriend = view.findViewById<TextView>(R.id.tvNewFriends)
        val addFriends = view.findViewById<ImageView>(R.id.add_friend)

        val contextReference = context
        if (contextReference is BottomBarVisibilityListener) {
            bottomBarListener = contextReference
        }
        bottomBarListener?.setBottomBarVisibility(this)

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


        return view
        // Inflate the layout for this fragment
    }
}
