package com.example.tranquitaskapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tranquitaskapp.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this);
        binding =  ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(SignUp())



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

    private fun replaceFragment(fragment: Fragment){
        val bottomBar = findViewById<BottomNavigationView>(R.id.bottomNavigationView) // Assurez-vous d'avoir l'ID correct
        val isAuthFragment = fragment is SignUp || fragment is SignIn

        if (isAuthFragment) {
            bottomBar.visibility = View.GONE // Cacher la Bottom Bar pour les fragments d'inscription ou de connexion
        } else {
            bottomBar.visibility = View.VISIBLE // Afficher la Bottom Bar pour d'autres fragments
        }

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout,fragment)
        fragmentTransaction.commit()
    }
}