package tranquitaskstudio.project.tranquitaskapp.fragment

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import tranquitaskstudio.project.tranquitaskapp.R
import tranquitaskstudio.project.tranquitaskapp.data.LeaderboardFilter
import tranquitaskstudio.project.tranquitaskapp.data.Friends
import tranquitaskstudio.project.tranquitaskapp.data.FriendsDictionary
import tranquitaskstudio.project.tranquitaskapp.data.Period
import tranquitaskstudio.project.tranquitaskapp.data.PeriodDictionary
import tranquitaskstudio.project.tranquitaskapp.firebase.MyFirebase
import tranquitaskstudio.project.tranquitaskapp.interfaces.BottomBarVisibilityListener


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

    }


    private fun setRadioButtons(view : View) {
        var buttonId : Int = 0

        // set period radio buttons
        val periodRadioGroup = view.findViewById<RadioGroup>(R.id.period_group)
        for ((period, option) in PeriodDictionary.periodToString) {
            val radioButton = RadioButton(context)
            radioButton.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            radioButton.text = option
            radioButton.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.black)) // Définit la couleur de teinte du bouton radio
            periodRadioGroup.addView(radioButton)
            if (period == LeaderboardFilter.period) {
//                radioButton.isChecked = true
                buttonId = radioButton.id
            }
        }

        periodRadioGroup.check(buttonId)

        periodRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedRadioButton = view.findViewById<RadioButton>(checkedId)

            if (selectedRadioButton != null) {
                val selectedValue = selectedRadioButton.text.toString()
                LeaderboardFilter.period = PeriodDictionary.stringToPeriod[selectedValue] ?: Period.ALL
            }
        }


        // set friends radio buttons
        val friendsRadioGroup = view.findViewById<RadioGroup>(R.id.friends_group)
        for ((friends, option) in FriendsDictionary.friendsToString) {
            val radioButton = RadioButton(context)
            radioButton.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            radioButton.text = option
            radioButton.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.black)) // Définit la couleur de teinte du bouton radio
            friendsRadioGroup.addView(radioButton)
            if (friends == LeaderboardFilter.friends) {
//                radioButton.isChecked = true
                buttonId = radioButton.id
            }
        }

        friendsRadioGroup.check(buttonId)

        friendsRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedRadioButton = view.findViewById<RadioButton>(checkedId)

            if (selectedRadioButton != null) {
                val selectedValue = selectedRadioButton.text.toString()
                LeaderboardFilter.friends = FriendsDictionary.stringToFriends[selectedValue] ?: Friends.FRIENDS
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_leaderboard_filter, container, false)

        val buttonBack = view.findViewById<ImageView>(R.id.back_arrow)

        val buttonPeriod = view.findViewById<ImageView>(R.id.button_period)
        val periodGroup = view.findViewById<RadioGroup>(R.id.period_group)

        val buttonFriends = view.findViewById<ImageView>(R.id.button_friends)
        val friendsGroup = view.findViewById<RadioGroup>(R.id.friends_group)


        buttonPeriod.setOnClickListener {
            // Inverser la visibilité du RadioGroup
            friendsGroup.visibility = View.GONE
            buttonFriends.setImageResource(R.drawable.arrow_down)
            periodGroup.visibility = if (periodGroup.visibility == View.VISIBLE) {
                View.GONE
            } else {
                View.VISIBLE
            }

            // Changer la flèche en fonction de la visibilité
            val arrowResource =
                if (periodGroup.visibility == View.VISIBLE) R.drawable.arrow_up else R.drawable.arrow_down
            buttonPeriod.setImageResource(arrowResource)
        }
        buttonFriends.setOnClickListener {
            // Inverser la visibilité du RadioGroup
            periodGroup.visibility = View.GONE
            buttonPeriod.setImageResource(R.drawable.arrow_down)
            friendsGroup.visibility = if (friendsGroup.visibility == View.VISIBLE) {
                View.GONE
            } else {
                View.VISIBLE
            }

            // Changer la flèche en fonction de la visibilité
            val arrowResource =
                if (friendsGroup.visibility == View.VISIBLE) R.drawable.arrow_up else R.drawable.arrow_down
            buttonFriends.setImageResource(arrowResource)
        }

        buttonBack.setOnClickListener {
            val fragment = Leaderboard()
            val slideUp = Slide(Gravity.BOTTOM)
            slideUp.duration = 150 // Durée de l'animation en millisecondes
            fragment.enterTransition = slideUp
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.frameLayout, fragment)?.commit()
        }

        val contextReference = context
        if (contextReference is BottomBarVisibilityListener) {
            bottomBarListener = contextReference
        }
        bottomBarListener?.setBottomBarVisibility(this)

        setRadioButtons(view)

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
            }
        } else {
            filterLeaderboardFriends.setImageResource(R.drawable.arrow_down)
            filterFriendsListView.visibility = View.GONE
        }
    }
}