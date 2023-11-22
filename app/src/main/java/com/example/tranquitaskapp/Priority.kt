package com.example.tranquitaskapp

import com.google.firebase.firestore.DocumentReference


data class Priority(
    var name: String,
    var value : Int
)

object PriorityDictionnary {
    var dictionary : HashMap<DocumentReference, Priority> = hashMapOf()
}