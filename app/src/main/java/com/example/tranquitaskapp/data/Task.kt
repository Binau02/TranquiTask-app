package com.example.tranquitaskapp.data

import com.google.firebase.firestore.DocumentReference


data class Task(
    var name: String,
    var concentration: Boolean,
    var divisible: Boolean,
    var done: Int,
    var duree: Int,
    var deadline: com.google.firebase.Timestamp?,
    var categorie: DocumentReference?,
    var priorite: Int,
    var ref : DocumentReference
)

object ListTask {
    var list : MutableList<Task> = mutableListOf()
}