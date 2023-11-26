package com.example.loginpageassignment.utilities.popup

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import com.example.loginpageassignment.R
import com.example.loginpageassignment.dataobjects.CurrentUser
import com.example.loginpageassignment.dataobjects.PSB_Event

class EventsPopup(private val context: Context) : DetailsPopup()
{
    private var alertDialog: AlertDialog? = null

    override fun showDetails(event: PSB_Event, user: CurrentUser)
    {
        val dialogBuilder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.event_details_popup, null)
        dialogBuilder.setView(dialogView)

        val eventNameTextView = dialogView.findViewById<TextView>(R.id.popupEventName)
        val eventTimeTextView = dialogView.findViewById<TextView>(R.id.popupEventTime)
        val eventDateTextView = dialogView.findViewById<TextView>(R.id.popupEventDate)
        val eventLocationTextView = dialogView.findViewById<TextView>(R.id.popupEventLocation)
        val viewOnMapButton = dialogView.findViewById<Button>(R.id.popupViewOnMapButton)
        val addToQueueButton = dialogView.findViewById<Button>(R.id.popupAddToQueueButton)
        val backButton = dialogView.findViewById<Button>(R.id.popupCloseButton)

        // Set event details
        eventNameTextView.text = event.eventName
        eventTimeTextView.text = event.eventTime
        eventDateTextView.text = event.eventDate
        eventLocationTextView.text = event.eventLocation

        // Button actions
        viewOnMapButton.setOnClickListener {
            //TODO: make function
        }

        addToQueueButton.setOnClickListener {
            //TODO: lookup location in database, create location object, pass to queue
            //QueueManager(user.username).addToQueue()
        }

        backButton.setOnClickListener {
            alertDialog?.dismiss()
        }

        val dialog = dialogBuilder.create()
        dialog.show()
        alertDialog = dialog
    }
}