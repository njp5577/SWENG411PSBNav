//EventPage.kt
package com.example.loginpageassignment.appscreens
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.loginpageassignment.R
import com.example.loginpageassignment.dataobjects.CurrentUser
import com.example.loginpageassignment.parentpageclasses.LoggedInPage
import com.example.loginpageassignment.dataobjects.PSB_Event
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.serialization.json.Json

class EventPage : LoggedInPage() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>

    private val eventRef = FirebaseFirestore.getInstance().collection("Events")
    private val events = mutableListOf<PSB_Event>()

    private var limit = 10 // Initial limit
    private var loading = false

    override fun onCreate(savedInstanceState: Bundle?)
    {
        Log.d("EventPage", "Entry")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)

        val userLogin = intent.getStringExtra("User")
        val user = Json.decodeFromString<CurrentUser>(userLogin.toString())
        setLoggedInAsFun(user)

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Adapter with CardView items
        adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>()
        {
            override fun onCreateViewHolder(parent: ViewGroup,
                                            viewType: Int): RecyclerView.ViewHolder
            {
                val view = LayoutInflater.from(parent.context).inflate(
                    R.layout.event_item,
                    parent,
                    false)
                return object : RecyclerView.ViewHolder(view) {}
            }

            override fun getItemCount(): Int
            {
                return events.size
            }

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
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                if (!loading && lastVisibleItemPosition == totalItemCount - 1) {
                    // Load more data when user scrolls to the bottom
                    loadMoreData()
                }
            }
        })
    }

    private fun getItem(position: Int): PSB_Event
    {
        // Return event data
        return events[position]
    }

    private fun bindEventData(eventCard: CardView, event: PSB_Event)
    {
        // Bind event data to the card view
        eventCard.findViewById<TextView>(R.id.dateTextView).text = event.eventDate
        eventCard.findViewById<TextView>(R.id.locationTextView).text = event.eventLocation
        eventCard.findViewById<TextView>(R.id.nameTextView).text = event.eventName
        eventCard.findViewById<TextView>(R.id.timeTextView).text = event.eventTime
    }

    private fun loadData()
    {
        loading = true
        eventRef.limit(limit.toLong()).get().addOnSuccessListener{ documents ->
            Log.d("EventPage", "Document count: ${documents.size()}")
            val newPSBEvents = mutableListOf<PSB_Event>()
            for (document in documents)
            {
                val event = document.toObject(PSB_Event::class.java)
                newPSBEvents.add(event)
            }

            loading = false
            updateAdapterData(newPSBEvents)
        }.addOnFailureListener{
            loading = false
            Log.e("EventPage", "Error loading data: $it")
        }
    }

    private fun loadMoreData()
    {
        limit += 10
        loadData()
    }

    //update the adapter data
    @SuppressLint("NotifyDataSetChanged")
    private fun updateAdapterData(newPSBEvents: List<PSB_Event>)
    {
        events.clear()
        events.addAll(newPSBEvents)
        adapter.notifyDataSetChanged()
    }
}