package tranquitaskstudio.project.tranquitaskapp.fragment

import android.content.Context
import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import tranquitaskstudio.project.tranquitaskapp.R
import tranquitaskstudio.project.tranquitaskapp.firebase.MyFirebase
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ProfileOther (val ref : DocumentReference,val friends: Boolean): Fragment() {
    val db = MyFirebase.getFirestoreInstance()
    private val decor: HashMap<String, String> = hashMapOf()

    private var username = ""
    private var coins : Int = 0
    private var profile_picture = ""

    private lateinit var view: View


    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    private fun replaceFragment(fragment: Fragment){
        val slideUp = Slide(Gravity.BOTTOM)
        slideUp.duration = 150 // Durée de l'animation en millisecondes
        fragment.enterTransition = slideUp
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameLayout, fragment)?.commit()
    }

    private suspend fun getProfile() {
        val userDoc = withContext(Dispatchers.IO) {
            Tasks.await(ref.get())
        }

        username = userDoc.getString("username") ?: ""
        coins = (userDoc.getLong("coins") ?: 0).toInt()
        profile_picture = userDoc.getString("profile_picture") ?: ""

        val categories = listOf(
            "sol",
            "maison",
            "arbre",
            "ciel"
        )

        for (category in categories) {
            decor[category] = userDoc.getString(category) ?: ""
        }

        setProfile()
    }

    private fun setProfile() {
        val profilePicture = view.findViewById<ImageView>(R.id.profileimage)

        if (profile_picture != "") {
            Glide.with(this)
                .load(profile_picture)
                .into(profilePicture)
        }

        val categories = mapOf<String, ImageView>(
            "sol" to view.findViewById(R.id.sol),
            "maison" to view.findViewById(R.id.maison),
            "arbre" to view.findViewById(R.id.arbre),
            "ciel" to view.findViewById(R.id.ciel)
        )

        for ((category, image) in categories) {
            if (decor[category] != "") {
                Glide.with(this)
                    .load(decor[category])
                    .into(image)
            }
        }

        view.findViewById<TextView>(R.id.tv_pseudo).text = username

        view.findViewById<TextView>(R.id.tvcoin_profile).text = coins.toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        view = inflater.inflate(R.layout.fragment_profile_other, container, false)
        val arrowReturn = view.findViewById<Button>(R.id.back2)
        arrowReturn.setOnClickListener{
            if(friends){
                replaceFragment(Friends())
            }else{
                replaceFragment(Leaderboard())
            }
        }

        lifecycleScope.launch {
            getProfile()
        }

        return view
    }

}