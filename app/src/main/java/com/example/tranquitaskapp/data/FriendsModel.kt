package com.example.tranquitaskapp.data

import com.google.firebase.firestore.DocumentReference

data class FriendsModel(
    val pseudo: String,
    val avatar: String,
    var ref: DocumentReference? = null
)