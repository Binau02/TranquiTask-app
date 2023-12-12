package com.example.tranquitaskapp.ui

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.tranquitaskapp.R

class CustomPopup {
    private var colorPrimary: Int = 0
    private var colorDark: Int = 0


    companion object {
        fun showPopup(context: Context, message: String, listener: PopupClickListener) {

            val colorPrimary = ContextCompat.getColor(context, R.color.my_primary_light)
            val colorDark = ContextCompat.getColor(context, R.color.my_dark)

            val inflater = LayoutInflater.from(context)
            val popupView = inflater.inflate(R.layout.pop_up, null)

            val width = ViewGroup.LayoutParams.WRAP_CONTENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            val focusable = true

            val popupWindow = PopupWindow(popupView, width, height, focusable)

            val textViewPopup = popupView.findViewById<TextView>(R.id.textViewPopup)
            textViewPopup.text = message

            val buttonClose = popupView.findViewById<Button>(R.id.buttonClose)
            buttonClose.setBackgroundColor(colorPrimary)
            buttonClose.setTextColor(colorDark)
            buttonClose.setOnClickListener {
                popupWindow.dismiss()

            }

            val buttonValidate = popupView.findViewById<Button>(R.id.buttonValidate)
            buttonValidate.setBackgroundColor(colorPrimary)
            buttonValidate.setTextColor(colorDark)
            buttonValidate.setOnClickListener {
                listener.onPopupButtonClick()
                popupWindow.dismiss()
            }

            popupWindow.showAtLocation(View(context), Gravity.CENTER, 0, 0)
        }
    }

    interface PopupClickListener {
        fun onPopupButtonClick()
    }
}