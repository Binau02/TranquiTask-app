package com.example.tranquitaskapp.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tranquitaskapp.R
import com.example.tranquitaskapp.adapter.ShopAdapter
import com.example.tranquitaskapp.data.ItemModel
import com.example.tranquitaskapp.data.User
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.FieldPosition

private enum class State {
    LOADING, SOL, MAISON, ARBRE, CIEL
}

class Shop : Fragment() {
    private val globalItems = hashMapOf<String, MutableList<ItemModel>>()
    private lateinit var rv: RecyclerView
    private var state : State = State.LOADING
    private val nameToState = mapOf(
        "sol" to State.SOL,
        "maison" to State.MAISON,
        "arbre" to State.ARBRE,
        "ciel" to State.CIEL
    )
    private val buttons = hashMapOf<String, RelativeLayout>()

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
                    for (item in result.items) {
                        var isBought = false
                        for (bought in User.bought[category]!!) {
                            if (item.name.replace(category, "").replace(".png", "") == bought) {
                                isBought = true;
                            }
                        }
                        val url = item.downloadUrl.await()
                        globalItems[category]?.add(
                            ItemModel(
                                image = url.toString(),
                                bought = isBought
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

    private fun select(position: Int) {

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_shop, container, false)

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

        lifecycleScope.launch {
            getItems()
        }

        return view
    }
}