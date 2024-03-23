package tranquitaskstudio.project.tranquitaskapp.firebase

import com.google.firebase.auth.FirebaseAuth

object MyFirebaseAuth {
    private var instance: FirebaseAuth? = null
    fun getFirestoreInstance(): FirebaseAuth {
        if (instance == null) {
            instance = FirebaseAuth.getInstance()
        }
        return instance!!
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }
}