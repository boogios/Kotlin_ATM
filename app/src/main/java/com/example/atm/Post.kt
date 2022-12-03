package com.example.atm

data class Post(
    var search: Search = Search(null, null, null, null, null, null, null, null, null, null),
    var currentNumberPeople: Int = 0,
    var requestNumberPeople: Int = 0,
    var comment: String = ""
)