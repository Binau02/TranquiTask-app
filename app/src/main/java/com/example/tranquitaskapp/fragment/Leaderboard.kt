package com.example.tranquitaskapp.fragment

import android.content.Context
import android.media.Image
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
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tranquitaskapp.data.CategoryDictionary
import com.example.tranquitaskapp.R
import com.example.tranquitaskapp.interfaces.BottomBarVisibilityListener
import com.example.tranquitaskapp.adapter.LeaderboardRowAdapter
import com.example.tranquitaskapp.data.Friends
import com.example.tranquitaskapp.data.FriendsModel
import com.example.tranquitaskapp.data.LeaderboardModel
import com.example.tranquitaskapp.data.LeaderboardFilter
import com.example.tranquitaskapp.data.Period
import com.example.tranquitaskapp.data.User
import com.example.tranquitaskapp.firebase.MyFirebase
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext



class Leaderboard : Fragment() {

    private val db = MyFirebase.getFirestoreInstance()

    private var bottomBarListener: BottomBarVisibilityListener? = null

    private lateinit var rv : RecyclerView
    private lateinit var textCategorie : TextView

    private val globalCategories: MutableList<Pair<String, DocumentReference?>> = mutableListOf()
    private var categorieIndex: Int = 0

    private val leaderboard: HashMap<DocumentReference?, List<LeaderboardModel>> = hashMapOf()

    private lateinit var firstPlaceLayout: LinearLayout
    private lateinit var secondPlaceLayout: LinearLayout
    private lateinit var thirdPlaceLayout: LinearLayout
    private lateinit var avatarFirstPlace: CircleImageView
    private lateinit var avatarSecondPlace: CircleImageView
    private lateinit var avatarThirdPlace: CircleImageView
    private lateinit var pseudoFirstPlace: TextView
    private lateinit var pseudoSecondPlace: TextView
    private lateinit var pseudoThirdPlace: TextView
    private lateinit var coinAmountFirstPlace: TextView
    private lateinit var coinAmountSecondPlace: TextView
    private lateinit var coinAmountThirdPlace: TextView

    private val globalFriends = mutableListOf<FriendsModel>()

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    private fun goToFriendsProfile(ref: DocumentReference){
        replaceFragment(ProfileOther(ref,false))
    }

    private fun replaceFragment(fragment: Fragment){
        val slideUp = Slide(Gravity.BOTTOM)
        slideUp.duration = 150 // Durée de l'animation en millisecondes
        fragment.enterTransition = slideUp
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameLayout, fragment)?.commit()
    }

    private fun onClickFiltre(){
        val fragment = LeaderboardFilter()
        val slideUp = Slide(Gravity.TOP)
        slideUp.duration = 150 // Durée de l'animation en millisecondes
        fragment.enterTransition = slideUp
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameLayout, fragment)?.commit()
    }

    private fun onClickChangeLeft(){
//        Toast.makeText(this.context, "Le bouton < a été cliqué !", Toast.LENGTH_SHORT).show()
        categorieIndex--
        if (categorieIndex == -1) {
            categorieIndex = globalCategories.size - 1
        }
        setLeaderboard()
    }
    private fun onClickChangeRight(){
//        Toast.makeText(this.context, "Le bouton > a été cliqué !", Toast.LENGTH_SHORT).show()
        categorieIndex++
        if (categorieIndex == globalCategories.size) {
            categorieIndex = 0
        }
        setLeaderboard()
    }
    private fun onClickText(){
//        Toast.makeText(this.context, "Le texte a été cliqué !", Toast.LENGTH_SHORT).show()
        categorieIndex = 0
        setLeaderboard()
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
        } catch (e: Exception) {
            Log.e("ERROR", "Error finding friend: $e")
        }
    }

    private fun addFriend(friendDoc: DocumentSnapshot?) {
        val name = friendDoc?.getString("username")
        val pp = friendDoc?.getString("profile_picture")
        if (name != null && pp != null) {
            globalFriends.add(FriendsModel(name, pp, friendDoc.reference))
        }
    }

    private suspend fun getLeaderboard() {
        val competitors : HashMap<DocumentReference?, HashMap<DocumentReference, LeaderboardModel>> = hashMapOf()

        competitors[null] = hashMapOf()
        globalCategories.add(Pair("Global", null))

        for ((categoryRef, category) in CategoryDictionary.dictionary) {
            competitors[categoryRef] = hashMapOf()
            globalCategories.add((Pair(category.name, categoryRef)))
        }

        if (LeaderboardFilter.friends == Friends.FRIENDS) {
            getFriends()
        }

        try {
            val transactions = withContext(Dispatchers.IO) {
                Tasks.await(db.collection("transaction").get())
            }
            for (transaction in transactions) {
                val amount = (transaction.getLong("amount") ?: 0).toInt()
                val date = transaction.getDate("date")
                val userRef = transaction.getDocumentReference("user")
                val categorieRef = transaction.getDocumentReference("categorie")

                val home = Home()

                if (userRef != null && date != null) {
                    if (LeaderboardFilter.friends != Friends.FRIENDS || globalFriends.find { it -> it.ref == userRef } != null) {
                        if (
                            LeaderboardFilter.period == Period.ALL
                            || (LeaderboardFilter.period == Period.DAY && home.isToday(Timestamp(date)))
                            || (LeaderboardFilter.period == Period.WEEK && home.isOnWeek(Timestamp(date)))
                        ) {
                            val refs = listOf(null, categorieRef)
                            for (ref in refs) {
                                if (competitors[ref] != null) {
                                    if (competitors[ref]!!.containsKey(userRef)) {
                                        competitors[ref]!![userRef]!!.coin =
                                            competitors[ref]!![userRef]!!.coin + amount
                                    } else {
                                        try {
                                            val user = withContext(Dispatchers.IO) {
                                                Tasks.await(userRef.get())
                                            }
                                            competitors[ref]!![userRef] = LeaderboardModel(
                                                pseudo = user.getString("username") ?: "",
                                                avatar = user.getString("profile_picture") ?: "",
                                                coin = amount,
                                                rank = ""
                                            )
                                        } catch (e: Exception) {
                                            Log.e("ERROR", "Error getting user : $e")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            onClickText()
        } catch (e : Exception) {
            Log.e("ERROR", "error getting transactions : $e")
        }

        for ((category, categoryCompetitors) in competitors) {
            leaderboard[category] = categoryCompetitors.values.sortedByDescending { it.coin }.take(10)
            var i = 0
            for (model in leaderboard[category]!!) {
                i++
                model.rank = "#" + i.toString()
            }
        }

        setLeaderboard()
    }

    private fun setLeaderboard() {
        val leaderboardList = leaderboard[globalCategories[categorieIndex].second] ?: listOf()

        textCategorie.text = globalCategories[categorieIndex].first

        avatarFirstPlace.setImageResource(R.drawable.default_profil_picture)
        avatarSecondPlace.setImageResource(R.drawable.default_profil_picture)
        avatarThirdPlace.setImageResource(R.drawable.default_profil_picture)
        pseudoFirstPlace.text = ""
        pseudoSecondPlace.text = ""
        pseudoThirdPlace.text = ""
        coinAmountFirstPlace.text = "0"
        coinAmountSecondPlace.text = "0"
        coinAmountThirdPlace.text = "0"

        if (leaderboardList.isNotEmpty()) {
            val firstPlace = leaderboardList[0]
            if (firstPlace.avatar.isNotEmpty()) {
                Glide.with(this)
                    .load(firstPlace.avatar)
                    .into(avatarFirstPlace)
            } else {
                avatarFirstPlace.setImageResource(R.drawable.default_profil_picture)
            }
            if (firstPlace.pseudo.isNotEmpty()) {
                pseudoFirstPlace.text = firstPlace.pseudo
            }
            coinAmountFirstPlace.text = firstPlace.coin.toString()
        }

        if (leaderboardList.size >= 2) {
            val secondPlace = leaderboardList[1]
            if (secondPlace.avatar.isNotEmpty()) {
                Glide.with(this)
                    .load(secondPlace.avatar)
                    .into(avatarSecondPlace)
            } else {
                avatarFirstPlace.setImageResource(R.drawable.default_profil_picture)
            }
            if (secondPlace.pseudo.isNotEmpty()) {
                pseudoSecondPlace.text = secondPlace.pseudo
            }
            coinAmountSecondPlace.text = secondPlace.coin.toString()
        }

        if (leaderboardList.size >= 3) {
            val thirdPlace = leaderboardList[2]
            if (thirdPlace.avatar.isNotEmpty()) {
                Glide.with(this)
                    .load(thirdPlace.avatar)
                    .into(avatarThirdPlace)
            } else {
                avatarFirstPlace.setImageResource(R.drawable.default_profil_picture)
            }
            if (thirdPlace.pseudo.isNotEmpty()) {
                pseudoThirdPlace.text = thirdPlace.pseudo
            }
            coinAmountThirdPlace.text = thirdPlace.coin.toString()
        }

        rv.adapter = LeaderboardRowAdapter(leaderboard[globalCategories[categorieIndex].second] ?: listOf(), this){ userRef ->
            goToFriendsProfile(userRef)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_leaderboard, container, false)
        rv = view.findViewById(R.id.rv_leaderboard)
        val buttonFiltre = view.findViewById<ImageView>(R.id.filtre)
        val buttonChangeL = view.findViewById<ImageView>(R.id.fleche_gauche)
        val buttonChangeR = view.findViewById<ImageView>(R.id.fleche_droite)
        textCategorie = view.findViewById(R.id.text_middle_categorie)


        avatarFirstPlace = view.findViewById(R.id.avatarFirst)
        avatarSecondPlace = view.findViewById(R.id.avatarSecond)
        avatarThirdPlace = view.findViewById(R.id.avatarThird)
        pseudoFirstPlace = view.findViewById(R.id.pseudoFirst)
        pseudoSecondPlace = view.findViewById(R.id.pseudoSecond)
        pseudoThirdPlace = view.findViewById(R.id.pseudoThird)
        coinAmountFirstPlace = view.findViewById(R.id.coin_amount_first)
        coinAmountSecondPlace = view.findViewById(R.id.coin_amount_second)
        coinAmountThirdPlace = view.findViewById(R.id.coin_amount_third)

        val contextReference = context
        if (contextReference is BottomBarVisibilityListener) {
            bottomBarListener = contextReference
        }
        bottomBarListener?.setBottomBarVisibility(this)

        lifecycleScope.launch {
            getLeaderboard()
        }

        buttonFiltre.setOnClickListener {
            onClickFiltre()
        }
        buttonChangeL.setOnClickListener {
            onClickChangeLeft()
        }
        buttonChangeR.setOnClickListener {
            onClickChangeRight()
        }
        textCategorie.setOnClickListener {
            onClickText()
        }


        rv.layoutManager = LinearLayoutManager(requireContext())
//        rv.adapter = LeaderboardRowAdapter(listLeaderboardModel) // Initialisez avec une liste vide ou vos données

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
         * @return A new instance of fragment Leaderboard.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Leaderboard().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

     */
}