package com.example.loginpageassignment.utilities.queue

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.loginpageassignment.appscreens.DestinationQueue
import com.example.loginpageassignment.dataobjects.Location
import com.google.firebase.firestore.FirebaseFirestore

//Singleton
//TODO: Fix refresh function after removal
class QueueManager private constructor(private val username: String, private val context: Context)
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

    fun removeFromQueue(location: Location?)
    {
        var locationRemoved = false

        queueRef.whereEqualTo("user", username).get().addOnSuccessListener { documents ->
            for (document in documents) {
                val existingLocations = document["list"]

                // Check if "locations" is not null and of the expected type
                if (existingLocations is MutableList<*>
                    && existingLocations.all { it is Map<*, *> })
                {
                    val locationList = existingLocations as MutableList<Map<String, Any>>

                    // If "locations" exists, find and remove the specified location
                    if (location != null)
                    {
                        val iterator = locationList.iterator()
                        while (iterator.hasNext())
                        {
                            val existingLocation = iterator.next()

                            // Compare the location properties for equality
                            if (existingLocation["name"] == location.name &&
                                existingLocation["desc"] == location.desc &&
                                existingLocation["latitude"] == location.latitude &&
                                existingLocation["longitude"] == location.longitude)
                            {
                                iterator.remove()
                                locationRemoved = true
                                break
                            }
                        }

                        // Update the document with the modified location list
                        queueRef.document(document.id).update("list", existingLocations)
                            .addOnSuccessListener {
                                if (locationRemoved)
                                {
                                    Log.d("Queue", "Location removed from the queue")
                                    // Refresh the page by restarting the activity
                                   // restartActivity()
                                }
                                else
                                    Log.d("Queue", "Location not found in the queue")
                            }
                            .addOnFailureListener { e ->
                                Log.e("Queue", "Error updating document", e)
                            }
                    }
                }
            }
        }
    }

    private fun restartActivity()
    {
        val intent = Intent(context, DestinationQueue::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra("User", username)
        context.startActivity(intent)
    }

    //shift up/down by +/- 1
    fun reorderQueue(shift: Int, position: Int): Boolean
    {
        //TODO: Write function
        return false
    }

    companion object
    {
        @SuppressLint("StaticFieldLeak")
        private var queueManager: QueueManager? = null
        fun getQueueManager(username: String, context: Context): QueueManager?
        {
            if (queueManager == null) queueManager = QueueManager(username, context)
            return queueManager
        }
    }
}