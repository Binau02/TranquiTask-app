package tranquitaskstudio.project.tranquitaskapp.fragment

import State
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tranquitaskstudio.project.tranquitaskapp.R
import tranquitaskstudio.project.tranquitaskapp.adapter.ShopAdapter
import tranquitaskstudio.project.tranquitaskapp.data.ItemModel
import tranquitaskstudio.project.tranquitaskapp.data.User
import com.google.firebase.firestore.FieldValue
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class Shop : Fragment() {
    private lateinit var view: View
    private val globalItems = hashMapOf<String, MutableList<ItemModel>>()
    private lateinit var rv: RecyclerView
    private var state : State = State.LOADING
    private val nameToState = mapOf(
        "sol" to State.SOL,
        "maison" to State.MAISON,
        "arbre" to State.ARBRE,
        "ciel" to State.CIEL
    )
    private val stateToName = mapOf(
        State.SOL to "sol",
        State.MAISON to "maison",
        State.ARBRE to "arbre",
        State.CIEL to "ciel"
    )
    private val buttons = hashMapOf<String, RelativeLayout>()
    private var selected: ItemModel? = null
    private var selectedFrame: RelativeLayout? = null

    private suspend fun getItems() {
        val storage = FirebaseStorage.getInstance()

        val folderPath = "decor/"
        val categories = listOf(
            "sol",
            "maison",
            "arbre",
            "ciel"
        )

        for (category in categories) {
            globalItems[category] = mutableListOf()

            val folderRef = storage.reference.child(folderPath + category)

            try {
                val result = folderRef.listAll().await()
                    var i = 0
                    for (item in result.items) {
                        i++
                        var isBought = false
                        for (bought in User.bought[category]!!) {
                            if (
                                item.name.replace(category, "")
                                    .replace(".png", "")
                                    .replace(".jpg", "")
                                == bought
                            ) {
                                isBought = true;
                            }
                        }
                        val url = item.downloadUrl.await()
                        globalItems[category]?.add(
                            ItemModel(
                                image = url.toString(),
                                bought = isBought,
                                index = i.toString()
                            )
                        )
                    }
            } catch (e : Exception) {
                Log.e("ERROR", "Error getting files in $category : $e")
            }
        }
        setItems("sol")
    }

    private fun setItems(category: String) {
        state = nameToState[category] ?: State.LOADING
        for (button in buttons.values) {
            button.setBackgroundResource(R.drawable.empty)
        }
        buttons[category]?.setBackgroundResource(R.drawable.bordered)

        rv.adapter = globalItems[category]?.let { ShopAdapter(it, this) }
    }

    private fun onClickCategory(name: String, relativeLayout: View) {
        if (state == State.LOADING || state == nameToState[name]) return

        setItems(name)
    }

    fun select(item: ItemModel, frame: RelativeLayout) {
        val image = view.findViewById<ImageView>(R.id.demo_image)
        val buyButton = view.findViewById<Button>(R.id.buy)

        selectedFrame?.setBackgroundResource(R.drawable.empty)

        if (item == selected || item.bought) {
            selected = null
            selectedFrame = null
            image.setImageResource(R.drawable.empty)
            buyButton.isEnabled = false
        }
        else {
            selected = item
            selectedFrame = frame
            frame.setBackgroundResource(R.drawable.rounded_rectangle)
            Glide.with(this)
                .load(item.image)
                .into(image)
            buyButton.isEnabled = true
        }
    }

    private fun buy() {
        if (selected == null) return

        if (User.coins < 500) {
            Toast.makeText(this.context, getString(R.string.not_enough_gold), Toast.LENGTH_SHORT).show()
            return
        }

        User.ref?.update(stateToName[state] + "_bought", FieldValue.arrayUnion(selected!!.index))
        User.bought[stateToName[state]]?.add(selected!!.index)
        User.ref?.update(mapOf("coins" to (User.coins - 500)))
        User.coins -= 500
        val mainActivity = MainActivity()
        mainActivity.refreshCoins()
        globalItems[stateToName[state]]?.get(selected!!.index.toInt() - 1)?.bought = true
        setItems(stateToName[state] ?: "sol")
        select(selected!!, selectedFrame!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        view = inflater.inflate(R.layout.fragment_shop, container, false)

        rv = view.findViewById(R.id.rv_items)
        rv.layoutManager = GridLayoutManager(requireContext(), 4)

        val solButton = view.findViewById<RelativeLayout>(R.id.sol)
        solButton.setOnClickListener {
            onClickCategory("sol", it)
        }
        buttons["sol"] = solButton

        val maisonButton = view.findViewById<RelativeLayout>(R.id.maison)
        maisonButton.setOnClickListener {
            onClickCategory("maison", it)
        }
        buttons["maison"] = maisonButton

        val arbreButton = view.findViewById<RelativeLayout>(R.id.arbre)
        arbreButton.setOnClickListener {
            onClickCategory("arbre", it)
        }
        buttons["arbre"] = arbreButton

        val cielButton = view.findViewById<RelativeLayout>(R.id.ciel)
        cielButton.setOnClickListener {
            onClickCategory("ciel", it)
        }
        buttons["ciel"] = cielButton

        view.findViewById<Button>(R.id.buy).setOnClickListener{
            buy()
        }

        lifecycleScope.launch {
            getItems()
        }

        return view
    }
}