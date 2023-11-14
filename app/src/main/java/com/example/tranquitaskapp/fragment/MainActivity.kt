package com.example.tranquitaskapp.fragment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.tranquitaskapp.R
import com.example.tranquitaskapp.databinding.ActivityMainBinding
import com.example.tranquitaskapp.navigation.BottomBarVisibilityListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity(), BottomBarVisibilityListener {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this);
        binding =  ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(SignIn())



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


    }

    override fun setBottomBarVisibility(fragment: Fragment) {
        val bottomBar = findViewById<BottomNavigationView>(R.id.bottomNavigationView) // Assurez-vous d'avoir l'ID correct

        val isAuthFragment = fragment is SignUp || fragment is SignIn
        bottomBar.visibility = if (isAuthFragment) View.GONE else View.VISIBLE
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout,fragment)
        fragmentTransaction.commit()
    }
}