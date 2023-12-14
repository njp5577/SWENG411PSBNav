package com.example.psbnavigator.model.dataobjects

import kotlinx.serialization.Serializable

//object to hold the current user
@Serializable
data class CurrentUser (var username: String)