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
import com.example.psbnavigator.controller.parentpageclasses.LoggedInPageAdmin
import com.example.psbnavigator.controller.utilities.managers.DatabaseManager
import com.example.psbnavigator.model.dataobjects.Location
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AdminLocDelete: LoggedInPageAdmin()
{

    private lateinit var editTextName: EditText
    private lateinit var buttonDelete: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
    private val locations = mutableListOf<Location>()
    private var limit = 5 // Initial limit
    private var loading = false

    // Reference to the "Locations" collection in Firestore
    private val locRef = DatabaseManager.getDatabaseManager()?.getLocationRef()

    override fun refresh()
    {
        val go = Intent(this, AdminLocDelete::class.java)
        val json = Json.encodeToString(getLoggedInAsFun())
        go.putExtra("User", json)
        startActivity(go)
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adminlocdelete)

        val userLogin = intent.getStringExtra("User")
        val user = Json.decodeFromString<CurrentUser>(userLogin.toString())
        setLoggedInAsFun(user)

        initializeView()

        //When user wants to sign up
        buttonDelete.setOnClickListener { handleDeleteLoc() }

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.locRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Adapter with CardView items
        adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>()
        {
            // Create ViewHolder
            override fun onCreateViewHolder(parent: ViewGroup,
                                            viewType: Int): RecyclerView.ViewHolder
            {
                val view = LayoutInflater.from(parent.context).inflate(
                    R.layout.search_result_item,
                    parent,
                    false)
                return object : RecyclerView.ViewHolder(view) {}
            }

            // Return the number of items in the list
            override fun getItemCount(): Int
            {
                return locations.size
            }

            // Bind data to ViewHolder
            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                val locCard = holder.itemView.findViewById<CardView>(R.id.eventCard)
                val locData = getItem(position)
                bindEventData(locCard, locData)
            }
        }

        recyclerView.adapter = adapter
        loadData()
        Log.d("Admin add location", "Locations size: ${locations.size}")

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
        buttonDelete = findViewById(R.id.buttonDelete)
    }

    private fun handleDeleteLoc()
    {
        val iname = editTextName.text.toString()

        //Check all users to look for a match
        locRef?.whereEqualTo("name", iname)?.get()?.addOnSuccessListener{ documents ->
            //Check if incorrect credentials
            if (documents.isEmpty) showToast("No location under this name.", this)
            else deleteLoc(documents)
        }
    }

    private fun deleteLoc(documents : QuerySnapshot)
    {
        documents.documents[0].reference.delete().addOnSuccessListener {
            showToast("The location under this name has been deleted.", this)
            refresh()
        }
    }

    // Get item at a specific position
    private fun getItem(position: Int): Location { return locations[position] }

    // Bind event data to the CardView
    private fun bindEventData(locationCard: CardView, loc: Location)
    {
        // Bind event data to the card view
        locationCard.findViewById<TextView>(R.id.nameTextView).text = loc.name
        locationCard.findViewById<TextView>(R.id.latitudeTextView).text = loc.latitude.toString()
        locationCard.findViewById<TextView>(R.id.longitudeTextView).text = loc.longitude.toString()
        locationCard.findViewById<TextView>(R.id.descriptionTextView).text = loc.desc
    }

    // Load initial data from database
    private fun loadData()
    {
        loading = true
        locRef?.limit(limit.toLong())?.get()?.addOnSuccessListener{ documents ->
            Log.d("EventPage", "Document count: ${documents.size()}")
            val newPSBLocations = mutableListOf<Location>()

            for (document in documents)  // Convert database documents to PSB_Event objects
            {
                val loc = document.toObject(Location::class.java)

                newPSBLocations.add(loc)

            }
            loading = false
            updateAdapterData(newPSBLocations)
        }?.addOnFailureListener{
            loading = false
            Log.e("EventPage", "Error loading data")
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
    private fun updateAdapterData(newPSBLocations: List<Location>)
    {
        locations.clear()
        locations.addAll(newPSBLocations)
        adapter.notifyDataSetChanged()
    }
}