package com.example.tranquitaskapp.fragment

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.tranquitaskapp.R
import com.example.tranquitaskapp.data.ListTask
import com.example.tranquitaskapp.data.MainActivityVariables
import com.example.tranquitaskapp.data.PeriodDictionary
import com.example.tranquitaskapp.data.Priorities
import com.example.tranquitaskapp.databinding.ActivityMainBinding
import com.example.tranquitaskapp.interfaces.BottomBarVisibilityListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.tranquitaskapp.data.User
import com.example.tranquitaskapp.firebase.MyFirebaseAuth
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity(), BottomBarVisibilityListener {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        binding =  ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(SignIn())
        val logonBtn = findViewById<ImageView>(R.id.deco)


        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home -> replaceFragment(Home())
                R.id.leaderboard -> replaceFragment(Leaderboard())
                R.id.profile -> replaceFragment(Profile())
                R.id.friends -> replaceFragment(Friends())
                R.id.add_task -> replaceFragment(AddTask())
                else ->{

                }
            }

            true
        }

        logonBtn.setOnClickListener {
            onClickSignOut()
            replaceFragment(SignIn())
        }
    }

    override fun setBottomBarVisibility(fragment: Fragment) {
        val bottomBar = findViewById<BottomNavigationView>(R.id.bottomNavigationView) // Assurez-vous d'avoir l'ID correct
        val header = findViewById<LinearLayout>(R.id.linearLayout) // Assurez-vous d'avoir l'ID correct

        val isAuthFragment = fragment is SignUp || fragment is SignIn || fragment is ForgotPassword
        bottomBar.visibility = if (isAuthFragment) View.GONE else View.VISIBLE
        header.visibility = if (isAuthFragment) View.GONE else View.VISIBLE

        MainActivityVariables.context = this
        refreshCoins()
    }

    fun refreshCoins() {
        val coinHeader = MainActivityVariables.context.findViewById<TextView>(R.id.tvcoin)

        coinHeader.text = User.coins.toString()
    }

    private fun onClickSignOut() {
        MyFirebaseAuth.signOut()
        PeriodDictionary.periodToString.clear()
        PeriodDictionary.stringToPeriod.clear()
        Priorities.dictionary.clear()
        Priorities.dictionary.putAll(
            mapOf(
                0 to "low",
                5 to "medium",
                10 to "high"
            )
        )
        Priorities.reversedDictionary.clear()
        ListTask.list.clear()

        // Naviguer vers l'écran de connexion ou effectuer d'autres actions après la déconnexion
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout,fragment)
        fragmentTransaction.commit()
    }
}