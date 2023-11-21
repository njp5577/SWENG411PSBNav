package com.example.loginpageassignment.dataobjects

import kotlinx.serialization.Serializable

@Serializable
data class Location (
    var name: String,
    var latitude: Double,
    var longitude: Double,
    var desc: String,
)