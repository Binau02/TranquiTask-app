package com.example.tranquitaskapp.data

import com.google.firebase.firestore.DocumentReference

object ListTaskFilter {
    var period: Period = Period.ALL
    var category: DocumentReference? = null
    var priority: Int = -1
}