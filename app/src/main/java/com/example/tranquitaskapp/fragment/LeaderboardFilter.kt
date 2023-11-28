package com.example.tranquitaskapp.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.tranquitaskapp.R
import com.example.tranquitaskapp.firebase.MyFirebase
import com.example.tranquitaskapp.interfaces.BottomBarVisibilityListener


class LeaderboardFilter : Fragment() {


    private val db = MyFirebase.getFirestoreInstance()
    private lateinit var rv: RecyclerView

    private var bottomBarListener: BottomBarVisibilityListener? = null

    private lateinit var badge : TextView

    private lateinit var filterLeaderboardFriends: ImageView
    private lateinit var filterLeaderboardPeriods: ImageView

    private var isListVisible = false // Variable pour suivre l'état actuel de la liste Périods
    private var isListFriendsVisible = false // Variable pour suivre l'état actuel de la liste Friends


    private var isArrowUpFriends = false // variable pour suivre l'état actuel de l'icône
    private var isArrowUp = false // variable pour suivre l'état actuel de l'icône

    private lateinit var filterPeriodListView: ListView
    private lateinit var filterFriendsListView: ListView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BottomBarVisibilityListener) {
            bottomBarListener = context
        }
        bottomBarListener?.setBottomBarVisibility(this)
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_leaderboard_filter, container, false)

        val buttonBack = view.findViewById<ImageView>(R.id.back_arrow)
        val buttonSave = view.findViewById<ImageView>(R.id.btn_saveFilterLeaderboard)
        filterLeaderboardFriends = view.findViewById(R.id.filter_leaderboardfriends)
        filterLeaderboardPeriods = view.findViewById(R.id.filter_leaderboardperiode)
        filterPeriodListView = view.findViewById(R.id.filterperiod)
        filterFriendsListView = view.findViewById(R.id.filterfriends)


        val itemListPeriod = arrayOf("Aujourd'hui", "Cette semaine", "Ce mois-ci", "Toujours")
        val adapterPeriod = ArrayAdapter(requireContext(), R.layout.custom_list_item, R.id.text1, itemListPeriod)
        filterPeriodListView.adapter = adapterPeriod

        val itemListFriends = arrayOf("Amis", "Global")
        val adapterFriends = ArrayAdapter(requireContext(), R.layout.custom_list_item, R.id.text1, itemListFriends)
        filterFriendsListView.adapter = adapterFriends

        buttonBack.setOnClickListener {
            val fragment = Leaderboard()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.frameLayout, fragment)?.commit()
        }
        buttonSave.setOnClickListener {
            Toast.makeText(this.context, "Le bouton Sauvegarde a été cliqué !", Toast.LENGTH_SHORT).show()
        }

        filterLeaderboardFriends.setOnClickListener {
            toggleListVisibilityFriends()
        }

        filterLeaderboardPeriods.setOnClickListener {
            toggleListVisibilityPeriod()
        }


        return view
        // Inflate the layout for this fragment
    }

    private fun toggleListVisibilityPeriod() {
        isListVisible = !isListVisible

        if (isListVisible) {
            filterLeaderboardPeriods.setImageResource(R.drawable.arrow_up)
            filterPeriodListView.visibility = View.VISIBLE
            if (isListFriendsVisible) {
                filterLeaderboardFriends.setImageResource(R.drawable.arrow_down)
                filterFriendsListView.visibility = View.GONE
                // Mettez ici le code pour charger ou mettre à jour la liste si nécessaire
            }        } else {
            filterLeaderboardPeriods.setImageResource(R.drawable.arrow_down)
            filterPeriodListView.visibility = View.GONE
        }
    }
    private fun toggleListVisibilityFriends() {
        isListFriendsVisible = !isListFriendsVisible

        if (isListFriendsVisible) {
            filterLeaderboardFriends.setImageResource(R.drawable.arrow_up)
            filterFriendsListView.visibility = View.VISIBLE
            if (isListVisible) {
                filterLeaderboardPeriods.setImageResource(R.drawable.arrow_down)
                filterPeriodListView.visibility = View.GONE
                // Mettez ici le code pour charger ou mettre à jour la liste si nécessaire
            }
            // Mettez ici le code pour charger ou mettre à jour la liste si nécessaire
        } else {
            filterLeaderboardFriends.setImageResource(R.drawable.arrow_down)
            filterFriendsListView.visibility = View.GONE
        }
    }
}