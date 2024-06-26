package tranquitaskstudio.project.tranquitaskapp.data

import com.google.firebase.firestore.DocumentReference

object User {
    var mail : String = ""
    var username : String = ""
    var coins : Long = 0
    var profile_picture = ""
    var id = ""
    var ref : DocumentReference? = null
    val bought = hashMapOf<String, MutableList<String>>()
    val decor = hashMapOf<String, String>(
        "sol" to "",
        "maison" to "",
        "arbre" to "",
        "ciel" to ""
    )
}