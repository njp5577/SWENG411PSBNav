//Location.kt
package com.example.loginpageassignment.dataobjects
import kotlinx.serialization.Serializable

//Serializable object that matches the location entry in the database
@Serializable
data class Location(
    val name: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val desc: String = ""
)