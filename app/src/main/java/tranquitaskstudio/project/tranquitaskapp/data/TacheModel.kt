package tranquitaskstudio.project.tranquitaskapp.data

import com.google.firebase.firestore.DocumentReference

data class TacheModel(
    val id:String,
    val name: String,
    val logoResId: Int,
    val progress: Int,
    var isDetail: Boolean,
    val duration: Int,
    val deadline: String,
    val priority: String,
    val category: String,
    val ref: DocumentReference,
    var done: Int,
    val concentration: Boolean,
    val isDivisible: Boolean
)