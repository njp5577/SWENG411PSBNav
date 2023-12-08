package com.example.loginpageassignment.dataobjects

import kotlinx.serialization.Serializable

//object to hold the current user
@Serializable
data class CurrentUser (var username: String)