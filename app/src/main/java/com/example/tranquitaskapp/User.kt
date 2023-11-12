package com.example.tranquitaskapp

object User {
    var mail : String = ""
    var username : String = ""
    var coins : Long = 0
    var profile_picture = ""
    var id = ""

    fun getUser():User{
        return this
    }
}