package com.example.loginpageassignment.utilities.managers

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.loginpageassignment.dataobjects.Location

//Singleton
class QueueManager private constructor(private val username: String, private val context: Context)
{
    private val queueRef = DatabaseManager.getDatabaseManager()?.getQueueRef()

    // Add a location to the user's queue
    fun addToQueue(location: Location?)
    {
        if (location == null) return  //early return for invalid location

        queueRef?.whereEqualTo("user", username)?.get()?.addOnSuccessListener { documents ->
            for (document in documents)
            {
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
                        updateQueue(document.id, existingLocations)
                    }
                }
            }
        }
    }

    // Check if the given object is a valid list of locations
    private fun isValidLocationList(existingLocations: Any?): Boolean
    {
        return existingLocations is MutableList<*> && existingLocations.all { it is Map<*, *> }
    }

    // Check if a location already exists in the given list
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

    // Add a new location to the list
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

    // Update the user's queue in Firestore
    private fun updateQueue(documentId: String, value: Any)
    {
        queueRef?.document(documentId)?.update("list", value)
            ?.addOnSuccessListener { Log.d("Queue", "Queue Updated") }
            ?.addOnFailureListener { e -> Log.e("Queue", "Error updating document", e) }
    }

    // Show a short toast message
    private fun showToast(message: String)
    {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    // Remove a location from the user's queue
    fun removeFromQueue(location: Location?)
    {
        if (location == null) return  //early return for invalid location

        queueRef?.whereEqualTo("user", username)?.get()?.addOnSuccessListener { documents ->
            for (document in documents)
            {
                val existingLocations = document["list"]

                if (isValidLocationList(existingLocations))
                {
                    val locationList = existingLocations as MutableList<Map<String, Any>>
                    val locationRemoved = removeLocation(location, locationList)
                    updateQueue(document.id, existingLocations)

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

    // Check if a location matches the given properties
    private fun existingMatches(location: Location, existingLocation: Map<String, Any>): Boolean
    {
        return existingLocation["name"] == location.name &&
                existingLocation["desc"] == location.desc &&
                existingLocation["latitude"] == location.latitude &&
                existingLocation["longitude"] == location.longitude
    }

    // Remove a location from the list
    private fun removeLocation(location: Location,
                               locationList: MutableList<Map<String, Any>>): Boolean
    {
        val iterator = locationList.iterator()
        while (iterator.hasNext())
        {
            val existingLocation = iterator.next()

            // Compare the location properties for equality
            if (existingMatches(location, existingLocation))
            {
                iterator.remove()
                return true
            }
        }
        return false
    }

    // Shift the position of a location in the user's queue (+1/-1)
    fun reorderQueue(shift: Int, location: Location)
    {
        queueRef?.whereEqualTo("user", username)?.get()?.addOnSuccessListener { documents ->
            for (document in documents)
            {
                val existingLocations = document["list"]
                if (isValidLocationList(existingLocations))
                {
                    val locationList = existingLocations as MutableList<Map<String, Any>>
                    val position = locationList.indexOfFirst { it["name"] == location.name }

                    if(checkShiftBounds(shift, position, locationList.size))
                    {
                        //swap positions
                        val tempLocation = locationList[position]
                        locationList[position] = locationList[position + shift]
                        locationList[position + shift] = tempLocation

                        //update queue
                        updateQueue(document.id, locationList)

                        // Refresh the user queue to ensure data consistency
                        refreshQueue()
                    }
                    else showToast("Cannot be moved any more in that direction")
                }
            }
        }?.addOnFailureListener{
            Log.d("QueueManager", "Failed to retrieve user queue: $it")
        }
    }

    private fun checkShiftBounds(shift: Int, position: Int, size: Int): Boolean
    {
        return !(position + shift < 0 || position + shift == size)
    }

    private fun refreshQueue()
    {
        queueRef?.whereEqualTo("user", username)?.get()?.addOnSuccessListener {
            Log.d("QueueManager", "User queue refreshed after reordering")
        }?.addOnFailureListener {
            Log.e("QueueManager", "Failed to refresh user queue: $it")
        }
    }

    // Companion object for creating a singleton instance of QueueManager
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