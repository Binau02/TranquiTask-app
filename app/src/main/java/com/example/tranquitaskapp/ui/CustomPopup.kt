package com.example.tranquitaskapp.ui

import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import com.example.tranquitaskapp.R

class CustomPopup {

    companion object {
        fun showPopup(context: Context, message: String, listener: PopupClickListener) {
            // Créer une instance de LayoutInflater
            val inflater = LayoutInflater.from(context)
            val popupView = inflater.inflate(R.layout.pop_up, null)

            // Créer une fenêtre popup
            val width = ViewGroup.LayoutParams.WRAP_CONTENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            val focusable = true // Permet au popup de recevoir des événements de focus en dehors de lui-même

            val popupWindow = PopupWindow(popupView, width, height, focusable)

            // Configurer les éléments du popup
            val textViewPopup = popupView.findViewById<TextView>(R.id.textViewPopup)
            textViewPopup.text = message

            val buttonClose = popupView.findViewById<Button>(R.id.buttonClose)
            buttonClose.setOnClickListener {
                // Fermer le popup
                popupWindow.dismiss()

                // Appeler la méthode du listener lorsque le bouton "Fermer" est cliqué
            }

            val buttonValidate = popupView.findViewById<Button>(R.id.buttonValidate)
            buttonValidate.setOnClickListener {
                // Fermer le popup
                // Appeler la méthode du listener lorsque le bouton "Valider" est cliqué
                listener.onPopupButtonClick()
                popupWindow.dismiss()
            }

            // Afficher le popup au centre de l'écran
            popupWindow.showAtLocation(View(context), Gravity.CENTER, 0, 0)
        }
    }

    // Interface pour le clic sur le bouton du popup
    interface PopupClickListener {
        fun onPopupButtonClick()
    }
}