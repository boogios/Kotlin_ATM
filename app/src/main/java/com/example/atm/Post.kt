package com.example.atm

data class Post(
    val search: Search,
    var currentNumberPeople: Int,
    val requestNumberPeople: Int,
    var comment: String
)
