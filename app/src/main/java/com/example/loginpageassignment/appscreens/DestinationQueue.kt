//DestinationQueue.kt
package com.example.loginpageassignment.appscreens
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock.sleep
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.loginpageassignment.R
import com.example.loginpageassignment.dataobjects.CurrentUser
import com.example.loginpageassignment.dataobjects.Location
import com.example.loginpageassignment.dataobjects.PSB_Event
import com.example.loginpageassignment.parentpageclasses.LoggedInPage
import com.example.loginpageassignment.utilities.managers.DatabaseManager
import com.example.loginpageassignment.utilities.popup.AddToQueuePopup
import com.example.loginpageassignment.utilities.managers.QueueManager
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// Serializable data class to represent the destination queue in the databse
@Serializable
data class DestQueue(var user: String, var list: MutableList<Location>)
{
    constructor() : this("", mutableListOf())  //error without constructor even if unused
}

//controller for Destination Queue page
class DestinationQueue : LoggedInPage(), AddToQueuePopup.PopupDismissCallback
{
    private lateinit var recyclerView: RecyclerView
    private lateinit var destQueueAdapter: DestQueueAdapter
    private lateinit var addToQueueButton: Button
    private var addToQueuePopup = AddToQueuePopup(this, this)

    //refreshes current page
    override fun refresh()
    {
        val go = Intent(this, DestinationQueue::class.java)
        val json = Json.encodeToString(getLoggedInAsFun())
        go.putExtra("User", json)
        startActivity(go)
    }

    //when activity is created
    override fun onCreate(savedInstanceState: Bundle?)
    {
        //assign content view
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_destinationqueue)

        //assign user credentials to activity
        val userLogin = intent.getStringExtra("User")
        val user = Json.decodeFromString<CurrentUser>(userLogin.toString())
        setLoggedInAsFun(user)

        //initialize recycler view
        recyclerView = findViewById(R.id.destQueueRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        destQueueAdapter = DestQueueAdapter(getLoggedInAsFun().username, this)
        recyclerView.adapter = destQueueAdapter

        //acquire reference to queue collection in database
        val queueRef = DatabaseManager.getDatabaseManager()?.getQueueRef()

        //add to queue button listener
        addToQueueButton = findViewById(R.id.addToQueueButton)
        addToQueueButton.setOnClickListener {
            //open popup
            addToQueuePopup.showDetails(PSB_Event(getLoggedInAsFun().username), getLoggedInAsFun())
        }

        //retreive queue for display
        queueRef?.get()?.addOnSuccessListener { documents ->
            if (documents.isEmpty) //handle the case where there's no data in the database
                Log.e("DestQueuePage", "No documents")
            else //process the documents to populate the destination queue
            {
                val destQueueList = documents.mapNotNull {
                    if (it.getString("user") == getLoggedInAsFun().username)
                    {
                        val rawList = it["list"] as ArrayList<Map<String, Any>>?
                        val list = rawList?.map { locationMap ->
                            Location(
                                locationMap["name"] as String,
                                locationMap["latitude"] as Double,
                                locationMap["longitude"] as Double,
                                locationMap["desc"] as String
                            )
                        }?.toMutableList()

                        DestQueue(getLoggedInAsFun().username, list ?: mutableListOf())
                    }
                    else null
                }

                //display locations in queue in recycler view
                if (destQueueList.isNotEmpty()) destQueueAdapter.setData(destQueueList[0].list)
                else showToast("No destinations in queue", this)
            }
        }?.addOnFailureListener{
            // Handle the failure to retrieve data from the database
            Log.d("DestQueuePage", "Error loading data: $it")
        }
    }

    // Adapter to bind data to RecyclerView
    class DestQueueAdapter(username : String, context: Context) :
        RecyclerView.Adapter<DestQueueAdapter.ViewHolder>()
    {
        private var destQueueList = mutableListOf<Location>()
        private val queueManager = QueueManager.getQueueManager(username, context)

        @SuppressLint("NotifyDataSetChanged")
        fun setData(data: MutableList<Location>) {
            destQueueList.clear()
            destQueueList.addAll(data)
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
        {
            // Inflate the layout for each item in the RecyclerView
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.dest_queue_item,
                parent,
                false
            )
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int)
        {
            if (destQueueList.isNotEmpty())
            {
                val destinationCard = holder.itemView.findViewById<CardView>(R.id.destCard)
                val destData = getItem(position, destQueueList)
                val refreshCallback: () -> Unit = {
                    (holder.itemView.context as DestinationQueue).refresh()
                }

                //bind data to the ViewHolder
                if (queueManager != null) holder.bind(destData, destinationCard, queueManager)

                //listeners for up and down buttons
                holder.itemView.findViewById<Button>(R.id.upButton).setOnClickListener {
                    queueManager?.reorderQueue(-1, destData)
                    sleep(250)
                    refreshCallback.invoke()
                }

                holder.itemView.findViewById<Button>(R.id.downButton).setOnClickListener {
                    queueManager?.reorderQueue(1, destData)
                    sleep(250)
                    refreshCallback.invoke()
                }
            }
        }

        override fun getItemCount(): Int { return destQueueList.size }

        //get item at a specific position or return default Location
        private fun getItem(position: Int, queue: MutableList<Location>): Location
        {
            if (position in 0 until queue.size) return queue[position]
            return Location()
        }

        //ViewHolder class to represent each item in the RecyclerView
        class ViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView)
        {
            private val refreshCallback: () -> Unit = {
                (itemView.context as DestinationQueue).refresh()
            }

            //bind data to views in the ViewHolder
            fun bind(destData: Location, destCard: CardView, queueManager: QueueManager)
            {
                destCard.findViewById<TextView>(R.id.nameTextView).text = destData.name

                destCard.findViewById<Button>(R.id.removeButton).setOnClickListener {
                    //grab location from card and pass to function
                    queueManager.removeFromQueue(destData)
                    sleep(250)
                    refreshCallback.invoke() // call refresh after removing an item
                }
            }
        }
    }

    override fun onPopupDismissed() { refresh() }
}