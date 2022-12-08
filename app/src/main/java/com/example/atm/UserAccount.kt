package com.example.atm

import java.io.Serializable

data class UserAccount(
    val emailID: String,
    val password: String,
    val gender: String,
    val nickName: String,
    val like: Int = 0,
    val chatRoom: String = "None"
) : Serializable
