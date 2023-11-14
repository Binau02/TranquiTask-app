package com.example.tranquitaskapp.data

data class TacheModel(
    val id:String,
    val name: String,
    val logoResId: Int,
    val progress: Int,
    var isDetail: Boolean,
    val duration: Int,
    val deadline: String,
    val priority: String,
    val category: String
)