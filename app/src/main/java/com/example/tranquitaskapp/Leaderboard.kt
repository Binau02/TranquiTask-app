package com.example.tranquitaskapp

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tranquitaskapp.navigation.BottomBarVisibilityListener
import com.example.tranquitaskapp.adapter.LeaderboardRowAdapter
import com.example.tranquitaskapp.data.LeaderboardModel
import com.example.tranquitaskapp.firebase.MyFirebase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Leaderboard.newInstance] factory method to
 * create an instance of this fragment.
 */
class Leaderboard : Fragment() {
    // TODO: Rename and change types of parameters
    /*
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var view: View
     */

    private val db = MyFirebase.getFirestoreInstance()
    private val listLeaderboardModel = mutableListOf<LeaderboardModel>(
        LeaderboardModel("Pseudo 1",R.drawable.arbre_removebg,"976", "#1"),
        LeaderboardModel("Pseudo 2",R.drawable.leaderboard_icon, "965", "#2"),
        LeaderboardModel("Pseudo 3",R.drawable.or, "875", "#3"),
        LeaderboardModel("Pseudo 4",R.drawable.leaderboard_icon, "834", "#4"),
        LeaderboardModel("Pseudo 5",R.drawable.arbre_removebg, "621", "#5"),
        LeaderboardModel("Pseudo 6",R.drawable.add, "608", "#6"),
        LeaderboardModel("Pseudo 7",R.drawable.or, "487", "#7"),
        LeaderboardModel("Pseudo 8",R.drawable.leaderboard_icon, "435", "#8"),
        LeaderboardModel("Pseudo 9",R.drawable.add, "384", "#9"),
        LeaderboardModel("Pseudo 10",R.drawable.arbre_removebg, "104", "#10"),
    )


    private var bottomBarListener: BottomBarVisibilityListener? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BottomBarVisibilityListener) {
            bottomBarListener = context
        }
        bottomBarListener?.setBottomBarVisibility(this)
    }

    fun onClickFiltre(){
        Toast.makeText(this.context, "Le bouton Filtre a été cliqué !", Toast.LENGTH_SHORT).show()
    }
    fun onClickChangeLeft(){
        Toast.makeText(this.context, "Le bouton < a été cliqué !", Toast.LENGTH_SHORT).show()
    }
    fun onClickChangeRight(){
        Toast.makeText(this.context, "Le bouton > un ami a été cliqué !", Toast.LENGTH_SHORT).show()
    }

    /*
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    } */

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.leaderboard, container, false)
        val rv: RecyclerView = view.findViewById(R.id.rv_leaderboard)
        val buttonFiltre = view.findViewById<Button>(R.id.filtre)
        val buttonChangeL = view.findViewById<Button>(R.id.fleche_gauche)
        val buttonChangeR = view.findViewById<Button>(R.id.fleche_droite)

        buttonFiltre.setOnClickListener {
            onClickFiltre()
        }
        buttonChangeL.setOnClickListener {
            onClickChangeLeft()
        }
        buttonChangeR.setOnClickListener {
            onClickChangeRight()
        }


        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = LeaderboardRowAdapter(listLeaderboardModel) // Initialisez avec une liste vide ou vos données

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