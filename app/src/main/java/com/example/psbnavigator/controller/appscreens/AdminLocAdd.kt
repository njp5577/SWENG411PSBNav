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
import com.example.psbnavigator.model.dataobjects.Location
import com.example.psbnavigator.controller.parentpageclasses.LoggedInPageAdmin
import com.example.psbnavigator.controller.utilities.managers.DatabaseManager
import com.example.psbnavigator.controller.utilities.popup.EventsManagePopup
import com.example.psbnavigator.model.dataobjects.PSB_Event
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AdminLocAdd : LoggedInPageAdmin()
{
    private lateinit var editTextName: EditText
    private lateinit var editTextLatitude: EditText
    private lateinit var editTextLongitude: EditText
    private lateinit var editTextDesc: EditText
    private lateinit var buttonAddLoc: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
    private val locations = mutableListOf<Location>()
    private var limit = 5 // Initial limit
    private var loading = false

    // Reference to the "Locations" collection in Firestore
    private val locRef = DatabaseManager.getDatabaseManager()?.getLocationRef()

    private fun getEditTextNameFun() : EditText{ return this.editTextName }

    private fun setEditTextNameFun(editTextName: EditText){ this.editTextName = editTextName }

    private fun getEditTextLatitudeFun() : EditText{ return this.editTextLatitude }

    private fun setEditTextLatitudeFun(editTextLatitude: EditText){ this.editTextLatitude = editTextLatitude }

    private fun getEditTextLongitudeFun() : EditText{ return this.editTextLongitude }

    private fun setEditTextLongitudeFun(editTextLongitude: EditText){ this.editTextLongitude = editTextLongitude }

    private fun getEditTextDescFun() : EditText{ return this.editTextDesc }

    private fun setEditTextDescFun(editTextDesc: EditText){ this.editTextDesc = editTextDesc }

    private fun getButtonAddLocFun() : Button{ return this.buttonAddLoc }

    private fun setButtonAddLocFun(buttonAddLoc: Button){ this.buttonAddLoc = buttonAddLoc }

    override fun refresh()
    {
        val go = Intent(this, AdminLocAdd::class.java)
        val json = Json.encodeToString(getLoggedInAsFun())
        go.putExtra("User", json)
        startActivity(go)
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adminlocadd)

        setEditTextNameFun(findViewById(R.id.editTextName))
        setEditTextLatitudeFun(findViewById(R.id.editTextLatitude))
        setEditTextLongitudeFun(findViewById(R.id.editTextLongitude))
        setEditTextDescFun(findViewById(R.id.editTextDesc))
        setButtonAddLocFun(findViewById(R.id.buttonAddLoc))

        val locRef = DatabaseManager.getDatabaseManager()?.getLocationRef()

        val userLogin = intent.getStringExtra("User")
        val user = Json.decodeFromString<CurrentUser>(userLogin.toString())
        setLoggedInAsFun(user)

        buttonAddLoc.setOnClickListener {
            val iname = getEditTextNameFun().text.toString()
            val ilat = getEditTextLatitudeFun().text.toString()
            val ilong = getEditTextLongitudeFun().text.toString()
            val idesc = getEditTextDescFun().text.toString()

            //Check that user is inputting appropriate number of characters for each field
            if (iname.length < 4)
            {
                showToast("Name must be at least 4 characters.", this)
                return@setOnClickListener
            }
            else if (idesc.length < 4)
            {
                showToast("Description must be at least 4 characters.", this)
                return@setOnClickListener
            }

            var lat = 0.0
            var long = 0.0

            try
            {
                lat = ilat.toDouble()
                long = ilong.toDouble()
            }
            catch(e: NumberFormatException)
            {
                showToast("Latitude and longitude must be decimal numbers.", this)
                return@setOnClickListener
            }

            locRef?.whereEqualTo("name", iname)?.get()?.addOnSuccessListener{ documents ->
                //Check that location does not already exist
                if (!(documents.isEmpty)) {
                    showToast("A location with this name already exists.", this)

                } else {
                    //Create location if there are no issues
                    locRef.add(Location(iname, lat, long, idesc)).addOnSuccessListener {
                        val message = "Location added at $lat, $long"
                        showToast(message, this)
                        refresh()
                    }
                }

            }

        }

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