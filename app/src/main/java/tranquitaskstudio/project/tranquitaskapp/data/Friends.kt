package tranquitaskstudio.project.tranquitaskapp.data

enum class Friends {
    GLOBAL, FRIENDS
}

object FriendsDictionary {
    val friendsToString : HashMap<Friends, String> = hashMapOf()
    val stringToFriends : HashMap<String, Friends> = hashMapOf()
}