package com.example.tranquitaskapp

object User {
    var mail : String = ""
    var username : String = ""
    var coins : Long = 0
    var profile_picture = ""

    fun getUser():User{
        return this
    }
}