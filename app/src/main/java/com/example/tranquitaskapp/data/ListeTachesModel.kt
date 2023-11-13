package com.example.tranquitaskapp.data

data class ListeTachesModel(
    val name: String,
    val logoResId: Int,
    val progress: Int,
    val isDetail: Boolean,
    val duration: Int,
    val deadline: String,
    val priority: String,
    val category: String
)