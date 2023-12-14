package com.example.psbnavigator.controller.utilities.popup

import android.app.AlertDialog
import android.content.Context
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.psbnavigator.R
import com.example.psbnavigator.controller.appscreens.EventOrgManageEvents
import com.example.psbnavigator.model.dataobjects.CurrentUser
import com.example.psbnavigator.model.dataobjects.Location
import com.example.psbnavigator.model.dataobjects.PSB_Event
import com.example.psbnavigator.controller.utilities.managers.DatabaseManager
import com.example.psbnavigator.controller.utilities.managers.QueueManager

class EventsManagePopup(private val context: Context) : DetailsPopup()
{
    private var alertDialog: AlertDialog? = null

    override fun showDetails(event: PSB_Event, user: CurrentUser)
    {
        val dialogBuilder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.event_manage_details_popup, null)
        dialogBuilder.setView(dialogView)

        val eventCreatorTextView = dialogView.findViewById<TextView>(R.id.popupEventCreator)
        val eventNameTextView = dialogView.findViewById<TextView>(R.id.popupEventName)
        val eventTimeTextView = dialogView.findViewById<TextView>(R.id.popupEventTime)
        val eventDateTextView = dialogView.findViewById<TextView>(R.id.popupEventDate)
        val eventLocationTextView = dialogView.findViewById<TextView>(R.id.popupEventLocation)
        val eventDescriptionTextView = dialogView.findViewById<TextView>(R.id.popupEventDescription)
        //val viewOnMapButton = dialogView.findViewById<Button>(R.id.popupViewOnMapButton)
        val addToQueueButton = dialogView.findViewById<Button>(R.id.popupAddToQueueButton)
        val removeButton = dialogView.findViewById<Button>(R.id.removeButton)
        val backButton = dialogView.findViewById<Button>(R.id.popupCloseButton)

        // Set event details
        eventCreatorTextView.text = event.eventCreator
        eventNameTextView.text = event.eventName
        eventTimeTextView.text = event.eventTime
        eventDateTextView.text = event.eventDate
        eventLocationTextView.text = event.eventLocation
        eventDescriptionTextView.text = event.eventDescription

        //viewOnMapButton.setOnClickListener { }

        addToQueueButton.setOnClickListener {
            val queueManager = QueueManager.getQueueManager(user.username, context)
            val locationRef = DatabaseManager.getDatabaseManager()?.getLocationRef()

            locationRef?.whereEqualTo("name", event.eventLocation)?.get()
                ?.addOnSuccessListener { documents ->
                    if(documents.isEmpty){
                        Toast.makeText(context, "This location does not exist anymore.", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        val location = documents.map{ doc -> doc.toObject(Location::class.java) }
                        queueManager?.addToQueue(location[0])
                        SystemClock.sleep(500)
                        alertDialog?.dismiss()
                    }
                }?.addOnFailureListener { exception ->
                Log.e("SearchPopup", "Error getting search results", exception)
            }
        }

        removeButton.setOnClickListener {
            val eventRef = DatabaseManager.getDatabaseManager()?.getEventRef()
            val icreator = event.eventCreator
            val iname = event.eventName
            val idate = event.eventDate
            val itime = event.eventTime
            val idesc = event.eventDescription
            val iloc = event.eventLocation

            val refreshCallback: () -> Unit = {
                (context as EventOrgManageEvents).refresh()
            }

            //Check all users to look for a match
            eventRef?.whereEqualTo("Event Creator", icreator)?.whereEqualTo("Name", iname)?.
                whereEqualTo("Date", idate)?.whereEqualTo("Time", itime)?.whereEqualTo("Description", idesc)?.whereEqualTo("Location", iloc)?.get()?.addOnSuccessListener{ documents ->
                //Check if incorrect credentials
                if (documents.isEmpty){
                    Toast.makeText(context, "Event does not exist.", Toast.LENGTH_SHORT).show()
                }
                else{
                    documents.documents[0].reference.delete().addOnSuccessListener {
                        Toast.makeText(context, "Successfully removed this event.", Toast.LENGTH_SHORT).show()
                        SystemClock.sleep(250)
                        refreshCallback.invoke()
                    }
                }
            }
        }

        backButton.setOnClickListener { alertDialog?.dismiss() }

        val dialog = dialogBuilder.create()
        dialog.show()
        alertDialog = dialog
    }
}