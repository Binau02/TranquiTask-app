package com.example.tranquitaskapp.fragment

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
import com.example.tranquitaskapp.R
import com.example.tranquitaskapp.adapter.EditDecorAdapter
import com.example.tranquitaskapp.adapter.ShopAdapter
import com.example.tranquitaskapp.data.ItemModel
import com.example.tranquitaskapp.data.User
import com.example.tranquitaskapp.interfaces.BottomBarVisibilityListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class EditDecor : Fragment() {
    private lateinit var view: View
    private val globalItems = hashMapOf<String, MutableList<ItemModel>>()
    private lateinit var rv: RecyclerView
    private var state : State = State.LOADING
    private val nameToId = mapOf(
        "sol" to R.drawable.herbe_removebg,
        "maison" to R.drawable.maison2_removebg,
        "arbre" to R.drawable.arbre_removebg,
        "ciel" to R.drawable.soleil_nuage
    )
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
    private var selected: HashMap<State, ItemModel?> = hashMapOf(
        State.SOL to null,
        State.MAISON to null,
        State.ARBRE to null,
        State.CIEL to null
    )
    private var selectedFrame: HashMap<State, RelativeLayout?> = hashMapOf(
        State.SOL to null,
        State.MAISON to null,
        State.ARBRE to null,
        State.CIEL to null
    )

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
                var i = 1
                globalItems[category]?.add(
                    ItemModel(
                        image = "",
                        bought = true,
                        index = "1"
                    )
                )
                for (item in result.items) {
                    i++
                    var isBought = false
                    for (bought in User.bought[category]!!) {
                        if (item.name.replace(category, "").replace(".png", "") == bought) {
                            isBought = true;
                        }
                    }
                    if (isBought) {
                        val url = item.downloadUrl.await()
                        globalItems[category]?.add(
                            ItemModel(
                                image = url.toString(),
                                bought = isBought,
                                index = i.toString()
                            )
                        )
                    }
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

        rv.adapter = globalItems[category]?.let { EditDecorAdapter(it, this, nameToId[category] ?: 0) }
    }

    private fun onClickCategory(name: String, relativeLayout: View) {
        if (state == State.LOADING || state == nameToState[name]) return

        setItems(name)
    }

    fun select(item: ItemModel, frame: RelativeLayout) {
        val image = view.findViewById<ImageView>(R.id.demo_image)

        selectedFrame[state]?.setBackgroundResource(R.drawable.empty)

        if (item == selected[state]) {
            selected[state] = null
            selectedFrame[state] = null
            image.setImageResource(R.drawable.empty)
        }
        else {
            selected[state] = item
            selectedFrame[state] = frame
            frame.setBackgroundResource(R.drawable.rounded_rectangle)
            Glide.with(this)
                .load(item.image)
                .into(image)
        }
    }

    private fun save() {
        for (s in enumValues<State>()) {
            if (s != State.LOADING) {
                val name = stateToName[s] ?: ""

                if (selected[s] != null) {
                    User.ref?.update(name, selected[s]?.image)
                    User.decor[name] = selected[s]?.image ?: ""
                }
            }
        }

        returnToProfile()
    }

    private fun cancel() {
        returnToProfile()
    }

    private fun returnToProfile() {
        val fragment = Profile()
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameLayout, fragment)?.commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        view = inflater.inflate(R.layout.fragment_edit_decor, container, false)

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

        view.findViewById<Button>(R.id.save).setOnClickListener{
            save()
        }

        view.findViewById<Button>(R.id.cancel).setOnClickListener{
            cancel()
        }

        lifecycleScope.launch {
            getItems()
        }

        return view
    }
}