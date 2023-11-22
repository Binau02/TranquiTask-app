package com.example.tranquitaskapp

import com.google.firebase.firestore.DocumentReference


data class Category(
    var name: String,
    var icon : String
)

object CategoryDictionnary {
    var dictionary : HashMap<DocumentReference, Category> = hashMapOf()
}