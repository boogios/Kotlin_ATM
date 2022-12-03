package com.example.atm

import java.io.Serializable

data class Join(
    var profileImage: Int,
    var nickname: String,
    var origin: String,
    var destination: String,
    var currentNumberPeople: Int,
    var requestNumberPeople: Int,
) : Serializable

