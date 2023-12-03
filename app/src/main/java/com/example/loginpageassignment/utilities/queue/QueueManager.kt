package com.example.loginpageassignment.utilities.queue

import android.util.Log
import com.example.loginpageassignment.dataobjects.Location
import com.google.firebase.firestore.FirebaseFirestore

//Singleton
class QueueManager private constructor(private val username: String)
{
    private val queueRef = FirebaseFirestore.getInstance().collection("Queues")
    fun addToQueue(location: Location?)
    {
        queueRef.whereEqualTo("user", username).get().addOnSuccessListener { documents ->
            for (document in documents) {
                val existingLocations = document["list"]

                // Check if "locations" is not null and of the expected type
                if (existingLocations is MutableList<*> && existingLocations.all { it is Map<*, *> }) {
                    val locationList = existingLocations as MutableList<Map<String, Any>>

                    // If "locations" exists, add the new location to it
                    if (location != null)
                    {
                        locationList.add(
                            mapOf(
                                "name" to location.name,
                                "desc" to location.desc,
                                "latitude" to location.latitude,
                                "longitude" to location.longitude
                            ))
                    }

                    // Update the document with the new location
                    queueRef.document(document.id).update("list", existingLocations)
                        .addOnSuccessListener {
                            Log.d("Queue", "Location added to the queue")
                        }
                        .addOnFailureListener { e ->
                            Log.e("Queue", "Error updating document", e)
                        }
                }
            }
        }
    }

    fun removeFromQueue(location: Location?): Boolean
    {
        //TODO: Write function
        return false
    }

    //shift up/down by +/- 1
    fun reorderQueue(shift: Int, position: Int): Boolean
    {
        //TODO: Write function
        return false
    }

    fun searchForElement(location: Location?): Int
    {
        //TODO: Write function
        return -1
    }

    companion object {
        private var queueManager: QueueManager? = null
        fun getQueueManager(username: String): QueueManager? {
            if (queueManager == null) queueManager = QueueManager(username)
            return queueManager
        }
    }
}