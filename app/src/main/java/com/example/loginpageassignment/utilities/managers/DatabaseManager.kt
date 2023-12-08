package com.example.loginpageassignment.utilities.managers

import android.annotation.SuppressLint
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

//Singleton class to manage all database collection references
class DatabaseManager
{
    private val database = FirebaseFirestore.getInstance()
    private val userRef = database.collection("Users");
    private val locationRef = database.collection("Locations");
    private val queueRef = database.collection("Queues");
    private val eventRef = database.collection("Events");

    // Companion object for creating a singleton instance of DatabaseManager
    companion object
    {
        @SuppressLint("StaticFieldLeak")
        private var databaseManager: DatabaseManager? = null
        fun getDatabaseManager(): DatabaseManager?
        {
            if (databaseManager == null) databaseManager = DatabaseManager()
            return databaseManager
        }
    }

    fun getUserRef(): CollectionReference { return this.userRef }

    fun getLocationRef(): CollectionReference { return this.locationRef }

    fun getQueueRef(): CollectionReference { return this.queueRef }

    fun getEventRef(): CollectionReference { return this.eventRef }
}