package com.example.tranquitaskapp.firebase

import com.google.firebase.firestore.FirebaseFirestore


object MyFirebase {
    private var instance: FirebaseFirestore? = null
    fun getFirestoreInstance(): FirebaseFirestore {
        if (instance == null) {
            instance = FirebaseFirestore.getInstance()
        }
        return instance!!
    }
}