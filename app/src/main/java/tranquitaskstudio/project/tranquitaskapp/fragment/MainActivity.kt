package tranquitaskstudio.project.tranquitaskapp.fragment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import tranquitaskstudio.project.tranquitaskapp.R
import tranquitaskstudio.project.tranquitaskapp.data.ListTask
import tranquitaskstudio.project.tranquitaskapp.data.MainActivityVariables
import tranquitaskstudio.project.tranquitaskapp.data.PeriodDictionary
import tranquitaskstudio.project.tranquitaskapp.data.Priorities
import tranquitaskstudio.project.tranquitaskapp.databinding.ActivityMainBinding
import tranquitaskstudio.project.tranquitaskapp.interfaces.BottomBarVisibilityListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import tranquitaskstudio.project.tranquitaskapp.data.User
import tranquitaskstudio.project.tranquitaskapp.firebase.MyFirebaseAuth
import tranquitaskstudio.project.tranquitaskapp.interfaces.MainActivityListener
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity(), BottomBarVisibilityListener, MainActivityListener {
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
        val bottomBar = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val header = findViewById<LinearLayout>(R.id.linearLayout)

        val isAuthFragment = fragment is SignUp || fragment is SignIn || fragment is ForgotPassword || fragment is StartTask
        val isLeaderboard = fragment is Leaderboard

        bottomBar.visibility = if (isAuthFragment) View.GONE else View.VISIBLE
        header.visibility = if (isAuthFragment||isLeaderboard) View.GONE else View.VISIBLE

        MainActivityVariables.context = this
        refreshCoins()
    }

    override fun refreshCoins() {
        val coinHeader = MainActivityVariables.context.findViewById<TextView>(R.id.tvcoin_profile)

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
        val slideUp = Slide(Gravity.END)
        slideUp.duration = 150 // Durée de l'animation en millisecondes
        fragment.enterTransition = slideUp
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout,fragment)
        fragmentTransaction.commit()
    }

    override fun onDestroy() {
        onClickSignOut()
        super.onDestroy()
    }
}