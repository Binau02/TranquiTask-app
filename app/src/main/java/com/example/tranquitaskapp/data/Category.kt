package com.example.tranquitaskapp.data

import com.google.firebase.firestore.DocumentReference


data class Category(
    var name: String,
    var icon : String
)

object CategoryDictionary {
    val dictionary : HashMap<DocumentReference, Category> = hashMapOf()
    val nameToDocumentReference : HashMap<String, DocumentReference> = hashMapOf()
}