package com.example.psbnavigator.controller.appscreens

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.psbnavigator.R
import com.example.psbnavigator.model.dataobjects.CurrentUser
import com.example.psbnavigator.model.dataobjects.PSB_Event
import com.example.psbnavigator.controller.parentpageclasses.LoggedInPageEventOrg
import com.example.psbnavigator.controller.utilities.managers.DatabaseManager
import com.example.psbnavigator.controller.utilities.popup.EventsManagePopup
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class EventOrgManageEvents : LoggedInPageEventOrg()
{

    private lateinit var editTextName: EditText
    private lateinit var editTextLocation: EditText
    private lateinit var editTextDate: EditText
    private lateinit var editTextTime: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var buttonEvent: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
    private val events = mutableListOf<PSB_Event>()
    private val eventsManagePopup = EventsManagePopup(this)
    private var limit = 5 // Initial limit
    private var loading = false

    private val eventRef = DatabaseManager.getDatabaseManager()?.getEventRef()

    public override fun refresh()
    {
        val go = Intent(this, EventOrgManageEvents::class.java)
        val json = Json.encodeToString(getLoggedInAsFun())
        go.putExtra("User", json)
        startActivity(go)
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eventorgmanageevents)

        val userLogin = intent.getStringExtra("User")
        val user = Json.decodeFromString<CurrentUser>(userLogin.toString())
        setLoggedInAsFun(user)

        initializeView()

        //When event org wants to add an event
        buttonEvent.setOnClickListener { handleEventAdd() }

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.eventManageRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Adapter with CardView items
        adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>()
        {
            // Create ViewHolder
            override fun onCreateViewHolder(parent: ViewGroup,
                                            viewType: Int): RecyclerView.ViewHolder
            {
                val view = LayoutInflater.from(parent.context).inflate(
                    R.layout.event_item,
                    parent,
                    false)
                return object : RecyclerView.ViewHolder(view) {}
            }

            // Return the number of items in the list
            override fun getItemCount(): Int
            {
                return events.size
            }

            // Bind data to ViewHolder
            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                val eventCard = holder.itemView.findViewById<CardView>(R.id.eventCard)
                val eventData = getItem(position)
                bindEventData(eventCard, eventData)
            }
        }

        recyclerView.adapter = adapter
        loadData()
        Log.d("EventPage", "Events size: ${events.size}")

        // Set up scroll listener
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int)
            {
                super.onScrolled(recyclerView, dx, dy)

                // Get the last visible item position
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                // Load more data when user scrolls to the bottom
                if (!loading && lastVisibleItemPosition == totalItemCount - 1) loadMoreData()
            }
        })
    }

    private fun initializeView()
    {
        editTextName = findViewById(R.id.editTextName)
        editTextLocation = findViewById(R.id.editTextLocation)
        editTextDate = findViewById(R.id.editTextDate)
        editTextTime = findViewById(R.id.editTextTime)
        editTextDescription = findViewById(R.id.editTextDescription)
        buttonEvent = findViewById(R.id.buttonEvent)
    }

    private fun handleEventAdd()
    {
        val iname = editTextName.text.toString()
        val ilocation = editTextLocation.text.toString()
        val idate = editTextDate.text.toString()
        val itime = editTextTime.text.toString()
        val idescription = editTextDescription.text.toString()
        val icreator = getLoggedInAsFun().username

        //Check that user is inputting appropriate number of characters for each field
        if (iname.length < 4)
        {
            showToast("Name must be at least 4 characters.", this)
        }
        else if (idescription.length < 4)
        {
            showToast("Description must be at least 4 characters.", this)
        }
        else
        {
            eventRef?.add(PSB_Event(icreator, iname, ilocation, idate, itime, idescription))
                ?.addOnSuccessListener {
                    val message = "Event added with name: $iname"
                    showToast(message, this)
                    refresh()
                }
        }
    }

    // Get item at a specific position
    private fun getItem(position: Int): PSB_Event { return events[position] }

    // Bind event data to the CardView
    private fun bindEventData(eventCard: CardView, event: PSB_Event)
    {
        // Bind event data to the card view
        eventCard.findViewById<TextView>(R.id.eventCreatorTextView).text = event.eventCreator
        eventCard.findViewById<TextView>(R.id.dateTextView).text = event.eventDate
        eventCard.findViewById<TextView>(R.id.locationTextView).text = event.eventLocation
        eventCard.findViewById<TextView>(R.id.nameTextView).text = event.eventName
        eventCard.findViewById<TextView>(R.id.timeTextView).text = event.eventTime

        // Show event details popup when the card is clicked
        eventCard.setOnClickListener{eventsManagePopup.showDetails(event, this.getLoggedInAsFun())}
    }

    // Load initial data from database
    private fun loadData()
    {
        loading = true
        eventRef?.limit(limit.toLong())?.get()?.addOnSuccessListener{ documents ->
            Log.d("EventPage", "Document count: ${documents.size()}")
            val newPSBEvents = mutableListOf<PSB_Event>()

            for (document in documents)  // Convert database documents to PSB_Event objects
            {
                val event = document.toObject(PSB_Event::class.java)
                if(event.eventCreator == getLoggedInAsFun().username){
                    newPSBEvents.add(event)
                }
            }

            // Sort the events by date and time
            newPSBEvents.sortWith(compareBy({ it.eventDate }, { it.eventTime }))
            loading = false
            updateAdapterData(newPSBEvents)
        }?.addOnFailureListener{
            loading = false
            Log.e("EventPage", "Error loading data: $it")
        }
    }

    // Load more data when the user scrolls to the bottom
    private fun loadMoreData()
    {
        limit += 5
        loadData()
    }

    // Update the adapter data
    @SuppressLint("NotifyDataSetChanged")
    private fun updateAdapterData(newPSBEvents: List<PSB_Event>)
    {
        events.clear()
        events.addAll(newPSBEvents)
        adapter.notifyDataSetChanged()
    }

}