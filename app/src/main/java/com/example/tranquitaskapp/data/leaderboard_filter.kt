package com.example.tranquitaskapp.data

import com.google.firebase.firestore.DocumentReference

object LeaderboardFilter {
    var period: Period = Period.ALL
    var category: DocumentReference? = null
    var priority: Int = -1
}