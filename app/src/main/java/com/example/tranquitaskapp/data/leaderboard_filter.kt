package com.example.tranquitaskapp.data

import com.google.firebase.firestore.DocumentReference

object LeaderboardFilter {
    var period: Period = Period.ALL
    var friends: Friends = Friends.FRIENDS
}

fun resetLeaderboardFilter(){
    LeaderboardFilter.period = Period.ALL
    LeaderboardFilter.friends = Friends.FRIENDS
}