package com.example.loginpageassignment.utilities.queue

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.loginpageassignment.dataobjects.Location
import com.google.firebase.firestore.FirebaseFirestore

//Singleton
class QueueManager private constructor(private val username: String, private val context: Context)
{
    private val queueRef = FirebaseFirestore.getInstance().collection("Queues")

    fun addToQueue(location: Location?)
    {
        if (location == null) return

        queueRef.whereEqualTo("user", username).get().addOnSuccessListener { documents ->
            for (document in documents) {
                val existingLocations = document["list"]

                // Check if locations are not null and of the expected type
                if (isValidLocationList(existingLocations))
                {
                    val locationList = existingLocations as MutableList<Map<String, Any>>

                    // Check if the location already exists in the list
                    if (locationAlreadyExists(location, locationList))
                    {
                        showToast("Location already exists within queue")
                    }
                    else // Add new location
                    {
                        addNewLocation(location, locationList)
                        showToast("${location.name} added to queue")
                        updateQueue(document.id, "list", existingLocations)
                    }
                }
            }
        }
    }

    private fun isValidLocationList(existingLocations: Any?): Boolean
    {
        return existingLocations is MutableList<*> && existingLocations.all { it is Map<*, *> }
    }

    private fun locationAlreadyExists(location: Location,
                                      locationList: MutableList<Map<String, Any>>): Boolean
    {
        return locationList.any {
            it["name"] == location.name &&
                    it["desc"] == location.desc &&
                    it["latitude"] == location.latitude &&
                    it["longitude"] == location.longitude
        }
    }

    private fun addNewLocation(location: Location, locationList: MutableList<Map<String, Any>>)
    {
        locationList.add(
            mapOf(
                "name" to location.name,
                "desc" to location.desc,
                "latitude" to location.latitude,
                "longitude" to location.longitude
            )
        )
    }

    private fun updateQueue(documentId: String, field: String, value: Any)
    {
        queueRef.document(documentId).update(field, value)
            .addOnSuccessListener { Log.d("Queue", "Queue Updated") }
            .addOnFailureListener { e -> Log.e("Queue", "Error updating document", e) }
    }

    private fun showToast(message: String)
    {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun removeFromQueue(location: Location?)
    {
        if (location == null) return

        queueRef.whereEqualTo("user", username).get().addOnSuccessListener { documents ->
            for (document in documents)
            {
                val existingLocations = document["list"]

                if (isValidLocationList(existingLocations))
                {
                    val locationList = existingLocations as MutableList<Map<String, Any>>
                    val locationRemoved = removeLocation(location, locationList)
                    updateQueue(document.id, "list", existingLocations)

                    if (locationRemoved)
                    {
                        Log.d("Queue", "Location removed from the queue")
                        showToast("${location.name} removed from queue")
                    }
                    else Log.d("Queue", "Location not found in the queue")
                }
            }
        }
    }

    private fun removeLocation(location: Location,
                               locationList: MutableList<Map<String, Any>>): Boolean {
        val iterator = locationList.iterator()
        while (iterator.hasNext()) {
            val existingLocation = iterator.next()

            // Compare the location properties for equality
            if (existingLocation["name"] == location.name &&
                existingLocation["desc"] == location.desc &&
                existingLocation["latitude"] == location.latitude &&
                existingLocation["longitude"] == location.longitude
            ) {
                iterator.remove()
                return true
            }
        }
        return false
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