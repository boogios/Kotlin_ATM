package com.example.atm

data class Search(
    var originName: String?=null,
    var originRoad: String?=null,
    var originAddress: String?=null,
    var originX: Double?=null,
    var originY: Double?=null,

    var destinationName: String?=null,
    var destinationRoad: String?=null,
    var destinationAddress: String?=null,
    var destinationX: Double?=null,
    var destinationY: Double?=null
)
