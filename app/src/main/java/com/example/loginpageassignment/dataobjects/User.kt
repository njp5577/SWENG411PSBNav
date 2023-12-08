package com.example.loginpageassignment.dataobjects

import kotlinx.serialization.Serializable

//Object for user information that matches the entry in the database
@Serializable
data class User (
    var name: String,
    var email: String,
    var username: String,
    var password: String,
    var type: String,
)